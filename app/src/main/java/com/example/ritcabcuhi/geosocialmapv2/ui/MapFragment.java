package com.example.ritcabcuhi.geosocialmapv2.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.example.ritcabcuhi.geosocialmapv2.api.GoogleDirectionApi;
import com.example.ritcabcuhi.geosocialmapv2.api.GooglePlaceApi;
import com.example.ritcabcuhi.geosocialmapv2.api.listener.ApiListener;
import com.example.ritcabcuhi.geosocialmapv2.api.listener.GoogleDirectionApiListener;
import com.example.ritcabcuhi.geosocialmapv2.api.PlaceApi;
import com.example.ritcabcuhi.geosocialmapv2.api.listener.GooglePlaceApiListener;
import com.example.ritcabcuhi.geosocialmapv2.eventbus.ChangePlacesTypeEvent;
import com.example.ritcabcuhi.geosocialmapv2.model.Direction;
import com.example.ritcabcuhi.geosocialmapv2.model.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapFragment";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final int RADIUS = 2000;
    private static final LatLng mDefaultLocation = new LatLng( 6.414422, 101.823475);
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;

    private boolean restored;
    private Uri imgFromGallery;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;

    private HashMap<String,Place> currentPlaceList;
    private HashMap<String,Place> myPlaceList;
    private HashMap<String,Place> healthPlaceList;
    private HashMap<String,Place> educationPlaceList;

    private ChangePlacesTypeEvent.PLACES_TYPE currentPlacesType;

    private LatLng selectedLatLng;
    private Marker selectedMarker;
    private List<MarkerOptions> markers;

    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    @BindView(R.id.btnShowAllPoint)
    FloatingActionButton btnShowAllLocation;
    @BindView(R.id.btnShowCurrentLocation)
    FloatingActionButton btnShowCurrentLocation;
    @BindView(R.id.btnNavigate)
    FloatingActionButton btnNavigate;
    @BindView(R.id.travelInfoContainer)
    LinearLayout travelInfoContainer;
    @BindView(R.id.textDistance)
    TextView textDistance;
    @BindView(R.id.textDuration)
    TextView textDuration;

    MaterialEditText inputName;
    MaterialEditText inputAddress;
    TextView inputPlaceImage;
    ImageView imageSelected;

    AlertDialog.Builder dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.survay_buttom,container,false);

        ButterKnife.bind(this,view);

        markers = new ArrayList<>();
        currentPlaceList = new HashMap<>();
        myPlaceList = new HashMap<>();
        healthPlaceList = new HashMap<>();
        educationPlaceList = new HashMap<>();

        currentPlacesType = ChangePlacesTypeEvent.PLACES_TYPE.MY_PLACES;

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if(savedInstanceState != null){
            Log.d(TAG, "onCreateView: restored");
            restored = true;
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        } else {
            Log.d(TAG, "onCreateView: not restored");
            restored = false;
        }


        try{
            SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_fragment);
            mapFragment.getMapAsync(this);
        }catch(NullPointerException ne){
            Log.e("test", "onCreateView: ", ne);
        }

        setHasOptionsMenu(true);

        placeApiInit();

        return view;

    }

    private void placeApiInit(){
        PlaceApi.getInstance().setListener(new ApiListener() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                myPlaceList.clear();

                Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = dataSnapshots.iterator();

                while(iterator.hasNext()){
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    Place place = next.getValue(Place.class);
                    myPlaceList.put(place.getId(),place);
                }

                markAllPoints();
            }

            @Override
            public void onFailure(DatabaseError error) {
                Log.d(TAG, "onFailure: " + error);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);

        }
    }

    @OnClick(R.id.btnShowAllPoint)
    public void showAllLocations(){
        if(!currentPlaceList.isEmpty()){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(getCurrentLatLng());
            for(Map.Entry<String, Place> entry : currentPlaceList.entrySet()) {
                Place place = entry.getValue();

                builder.include(place.getLatLng());
            }
            LatLngBounds bounds;
            bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,180));
        }
    }

    public void showPairLocations(){
        if(!currentPlaceList.isEmpty() && selectedLatLng != null){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(getCurrentLatLng());
            builder.include(selectedLatLng);

            LatLngBounds bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,180));
        }
    }


    @OnClick(R.id.btnShowCurrentLocation)
    public void showCurrentLocation(){
        if(mLastKnownLocation != null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
    }

    @OnClick(R.id.btnNavigate)
    public void navigate(){
        if(selectedLatLng != null){
            GoogleDirectionApi.getInstance().findPath(getCurrentLatLng(),selectedLatLng).setListener(new GoogleDirectionApiListener() {
                @Override
                public void onSuccess(Direction direction) {
                    Log.d(TAG, "distance : " + direction.getDistance());
                    Log.d(TAG, "duration : " + direction.getDuration());
                    if(direction.getPath() != null) {
                        showPairLocations();
                        mMap.clear();
                        markAllPoints();
                        drawLine(direction.getPath());
                        travelInfoContainer.setVisibility(View.VISIBLE);
                        textDistance.setText("ระยะทาง: " + createTextDistance(direction.getDistance()));
                        textDuration.setText("ระยะเวลา: " + createTextDuration(direction.getDuration()));
                    }
                }
            });
        }
    }

    private String createTextDistance(int distance){
        if(distance<1000)
            return distance + " เมตร";
        else
            return Math.round(distance/1000f) + " กม.";
    }

    private String createTextDuration(int duration){
        int hours = Math.round(duration/3600);
        int minutes = Math.round(duration/60);
        String durationText = "";
        if(hours>0){
            durationText = hours + " " + "ชั่วโมง" + " ";
            minutes = Math.round(duration%3600/60);
        }
        durationText = durationText + minutes + " นาที";
        return durationText;
    }

    private void drawLine(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        // Traversing through all the routes
        for(int i=0;i<result.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(8);
            lineOptions.color(getActivity().getResources().getColor(R.color.colorPrimary));
        }

        // Drawing polyline in the Google Map for the i-th route
        if(lineOptions!=null)
        mMap.addPolyline(lineOptions);
    }

    private LatLng getCurrentLatLng(){
        return new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        getLocationPermission();
        getDeviceLocation();

        updateLocationUI();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                unSelect();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                selectedLatLng = marker.getPosition();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        marker.showInfoWindow();
                    }
                },500);
                return false;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.layout_info_window, (FrameLayout)getView().findViewById(R.id.map), false);
                TextView textPlaceName = infoWindow.findViewById(R.id.textPlaceName);
                TextView textPlaceAddress = infoWindow.findViewById(R.id.textPlaceAddress);
                ImageView infoWindowIv = infoWindow.findViewById(R.id.imageInfoWindow);

                try{
                    Place place = currentPlaceList.get(marker.getSnippet());
                    textPlaceName.setText(place.getName());
                    textPlaceAddress.setText(place.getAddress());
                    Glide.with(getActivity()).load(place.getImageUri()).into(infoWindowIv);

                    Log.d(TAG, "getInfoContents: " + place.getAddress());
                }catch (Exception e){
                    Log.e(TAG, "getInfoContents: ",e );
                }

                return infoWindow;
            }
        });

        if(restored)
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));
    }

    private void getDeviceLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                            if(!restored)
                                showCurrentLocation();
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission(){
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_option, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void setupDialog(){
        dialog = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.layout_add_place_dialog, null);
        dialog.setView(view);

        inputName = view.findViewById(R.id.inputName);
        inputAddress = view.findViewById(R.id.inputAddress);
        inputPlaceImage = view.findViewById(R.id.inputPlaceImage);
        imageSelected = view.findViewById(R.id.imageSelected);

        inputPlaceImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                selectedMarker.setVisible(false);
            }
        });

        dialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uploadImage();
            }
        });

        dialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedMarker.setVisible(false);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            imgFromGallery = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgFromGallery);
                imageSelected.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            inputPlaceImage.setText("เปลี่ยนรูป");
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if(currentPlacesType == ChangePlacesTypeEvent.PLACES_TYPE.MY_PLACES){
            MarkerOptions marker = new MarkerOptions().position(latLng);
            selectedLatLng = latLng;
            selectedMarker = mMap.addMarker(marker);

            setupDialog();
            dialog.show();
        }
    }

    String generateImageUrl(Place place){
        return "profileImage/"+ place.getId() + ".jpg";
    }

    private void uploadImage() {
        final String name = inputName.getText().toString();
        final String address = inputAddress.getText().toString();

        if(name.isEmpty()){
            Toast.makeText(getContext(), "กรุณาเพิ่มชื่อสถานที่", Toast.LENGTH_SHORT).show();
            return;
        }

        if(address.isEmpty()){
            Toast.makeText(getContext(), "กรุณาเพิ่มบ้านเลขที่", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imgFromGallery == null){
            Toast.makeText(getContext(), "กรุณาเพิ่มรูปภาพ", Toast.LENGTH_SHORT).show();
            return;
        }

        final Place place = new Place();
        place.setName(name);
        place.setAddress(address);
        place.setLatitude(selectedLatLng.latitude);
        place.setLongitude(selectedLatLng.longitude);
        place.setImageUrl(generateImageUrl(place));

        StorageReference storage = FirebaseStorage.getInstance().getReference();

        if(imgFromGallery != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("กำลังอัพโหลด...");
            progressDialog.show();

            StorageReference ref = storage.child(place.getImageUrl());
            ref.putFile(imgFromGallery).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "อัพโหลดสำเร็จ", Toast.LENGTH_SHORT).show();

                            PlaceApi.getInstance().createPlace(place);

                            inputAddress.setText("");
                            imgFromGallery = null;
                            inputPlaceImage.setText("เปลี่ยนรูป");

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "ไม่สำเร็จ "+e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onFailure: ", e);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("อัพโหลด "+(int)progress+"%");
                        }
                    });
        }
    }

    private void markAllPoints(){
        clearMap();
        travelInfoContainer.setVisibility(View.INVISIBLE);

        for(Map.Entry<String, Place> entry : currentPlaceList.entrySet()) {
            String key = entry.getKey();
            Place place = entry.getValue();

            MarkerOptions options = new MarkerOptions().position(place.getLatLng()).title(place.getAddress()).snippet(key);
            markers.add(options);

            mMap.addMarker(options);
        }
    }

    private void clearMap(){
        mMap.clear();
        markers.clear();
        unSelect();
    }

    private void unSelect(){
        selectedLatLng = null;
        selectedMarker = null;
    }

    @Subscribe
    public void onChangePlacesType(ChangePlacesTypeEvent e){
        Log.d(TAG, "onChangePlacesType: " + e.getPlacesType());
        currentPlacesType = e.getPlacesType();
        switch (e.getPlacesType()){
            case MY_PLACES:
                currentPlaceList = myPlaceList;
                markAllPoints();
                break;
            case HEALTH:
                GooglePlaceApi.getInstace().getNeaybyPlace(getCurrentLatLng(),"hospital",RADIUS).setListener(new GooglePlaceApiListener() {
                    @Override
                    public void onSuccess(HashMap<String,Place> placeList) {

                        try{
                            for(Map.Entry<String,Place> place: placeList.entrySet()){
                                String key = place.getKey();
                                Place value = place.getValue();

                                Log.d(TAG, "onSuccess: " + value.getName());
                                Log.d(TAG, "onSuccess: " + value.getImageUrl());
                            }

                            healthPlaceList = placeList;
                            currentPlaceList = healthPlaceList;
                            markAllPoints();
                            Toast.makeText(getContext(), "แสดงพิกัดสถานสุขภาพ", Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Log.e(TAG, "onSuccess: ",e );
                            Toast.makeText(getContext(), "ไม่พบสถานสุขภาพ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                break;
            case EDUCATION:
                GooglePlaceApi.getInstace().getNeaybyPlace(getCurrentLatLng(),"school",RADIUS).setListener(new GooglePlaceApiListener() {
                    @Override
                    public void onSuccess(HashMap<String, Place> placeList) {
                        try{
                            for(Map.Entry<String,Place> place: placeList.entrySet()){
                                String key = place.getKey();
                                Place value = place.getValue();

                                Log.d(TAG, "onSuccess: " + value.getName());
                                Log.d(TAG, "onSuccess: " + value.getImageUrl());
                            }

                            educationPlaceList = placeList;
                            currentPlaceList = educationPlaceList;
                            markAllPoints();
                            Toast.makeText(getContext(), "แสดงพิกัดสถานศึกษา", Toast.LENGTH_SHORT).show();
                        }catch(Exception e){
                            Log.e(TAG, "onSuccess: ",e );
                            Toast.makeText(getContext(), "ไม่พบสถานศึกษา", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
