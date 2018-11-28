package com.example.ritcabcuhi.geosocialmapv2.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.ritcabcuhi.geosocialmapv2.R;
import com.example.ritcabcuhi.geosocialmapv2.model.Place;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "HomeFragment";

    private final String placeTablePath = "/Place";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int DEFAULT_ZOOM = 15;
    private static final LatLng mDefaultLocation = new LatLng( 6.414422, 101.823475);
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted = false;

    private boolean restored;
    private Uri imgFromGallery;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;

    private List<Place> placeList;
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

    MaterialEditText inputAddress;
    TextView inputPlaceImage;
    ImageView imageSelected;

    View infoWindow;
    TextView infoWindowTv;
    ImageView infoWindowIv;


    AlertDialog.Builder dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.survay_buttom,container,false);

        ButterKnife.bind(this,view);

        markers = new ArrayList<>();
        placeList = new ArrayList<>();
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

        //info window
        infoWindow = getLayoutInflater().inflate(R.layout.layout_info_window, (FrameLayout)view.findViewById(R.id.map), false);
        infoWindowTv = infoWindow.findViewById(R.id.textInfoWindow);
        infoWindowIv = infoWindow.findViewById(R.id.imageInfoWindow);

//        Toolbar toolbar = toolbar.findViewById();
//        setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        setPlaceDBListener();

        return view;

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

    }

    @OnClick(R.id.btnShowCurrentLocation)
    public void showCurrentLocation(){
        if(mLastKnownLocation != null)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
    }

    @OnClick(R.id.btnNavigate)
    public void navigate(){

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLongClickListener(this);

        getLocationPermission();
        getDeviceLocation();

        updateLocationUI();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                final String url = marker.getSnippet();

//                FirebaseStorage.getInstance().getReference().child(url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
////                        marker.hideInfoWindow();
//                        Log.d(TAG, "onSuccess: uri = " + uri);
//                        Glide.with(getActivity()).load(uri).into(infoWindowIv);
//
//                        marker.hideInfoWindow();
//                        marker.showInfoWindow();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e(TAG, "onFailure: ",e );
//                    }
//                });


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

                infoWindowTv.setText(marker.getTitle());

                Glide.with(getActivity()).load("https://firebasestorage.googleapis.com/v0/b/geosocialmap.appspot.com/o/images%2F069294a6-b899-452e-94ab-2c40c87f3de2.jpg?alt=media&token=bc466309-9da8-443c-8692-7f73e5715bfd").into(infoWindowIv);

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
        MarkerOptions marker = new MarkerOptions().position(latLng);
        selectedLatLng = latLng;
        selectedMarker = mMap.addMarker(marker);

        setupDialog();
        dialog.show();
    }

    public void setPlaceDBListener(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference placeTable = db.getReference(placeTablePath);
        placeTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                markers.clear();
                placeList.clear();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();

                    Place place = next.getValue(Place.class);
                    Log.d(TAG, "onDataChange: " + place.getAddress());
                    markers.add(new MarkerOptions().position(place.getLatLng())
                            .title(place.getAddress())
                            .snippet(place.getImageUrl()));
                    placeList.add(place);
                }

                markAllPoints();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void uploadImage() {

        final String address = inputAddress.getText().toString();
        final String storagePath = "images/"+ UUID.randomUUID().toString() + ".jpg";

        Log.d(TAG, "uploadImage: storage upload : " + storagePath);

        if(address.isEmpty()){
            Toast.makeText(getContext(), "กรุณาเพิ่มบ้านเลขที่", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imgFromGallery == null){
            Toast.makeText(getContext(), "กรุณาเพิ่มรูปภาพ", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storage = FirebaseStorage.getInstance().getReference();

        if(imgFromGallery != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("กำลังอัพโหลด...");
            progressDialog.show();

            StorageReference ref = storage.child(storagePath);
            ref.putFile(imgFromGallery)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "อัพโหลดสำเร็จ", Toast.LENGTH_SHORT).show();

                            addPlacetoDB(address,storagePath,selectedLatLng);

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

    public void addPlacetoDB(String address, String imageUrl,LatLng latLng){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference placeTable = db.getReference(placeTablePath);

        String key = placeTable.push().getKey();
        Place place = new Place(address,imageUrl,latLng.latitude,latLng.longitude);

        placeTable.child(key).setValue(place);
    }

    private void markAllPoints(){
        mMap.clear();
        for(MarkerOptions mark: markers){
            mMap.addMarker(mark);
        }
    }
}
