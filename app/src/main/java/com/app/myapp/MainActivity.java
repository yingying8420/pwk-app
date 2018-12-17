package com.app.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {
    MapView mMapView = null;
    private static final String LOG_TAG = "HelloCamera";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private Button takePicBtn = null;
    private ImageView imageView = null;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        AMap aMap = null;
        if (aMap == null) {
            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
            aMap = mMapView.getMap();
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        }
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        takePicBtn = (Button) findViewById(R.id.buttonPicture);
        takePicBtn.setOnClickListener(takePiClickListener);//调用具体方法

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    //调用相机
    public void onCamera() {
        Log.d(LOG_TAG, "Take Picture Button Click");
        // 利用系统自带的相机应用:拍照
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a file to save the image
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        // 此处这句intent的值设置关系到后面的onActivityResult中会进入那个分支，即关系到data是否为null，如果此处指定，则后来的data为null
        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private final View.OnClickListener takePiClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, 222);
                    return;
                } else {
                    onCamera();
                }
            } else {
                onCamera();
            }
        }

    };

    public static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * Create a file Uri for saving an image or video
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = null;
        try {
            // This location works best if you want the created images to be
            // shared
            // between applications and persist after your app has been
            // uninstalled.
            mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "MyCameraApp");

            Log.d(LOG_TAG, "Successfully created mediaStorageDir: "
                    + mediaStorageDir);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "Error in Creating mediaStorageDir: "
                    + mediaStorageDir);
        }

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // 在SD卡上创建文件夹需要权限：
                // <uses-permission
                // android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                Log.d(LOG_TAG,
                        "failed to create directory, check if you have the WRITE_EXTERNAL_STORAGE permission");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult: requestCode: " + requestCode
                + ", resultCode: " + requestCode + ", data: " + data);
        // 拍照
        if (CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE == requestCode) {
            Log.d(LOG_TAG, "CAPTURE_IMAGE");

            if (RESULT_OK == resultCode) {
                Log.d(LOG_TAG, "RESULT_OK");

                // Check if the result includes a thumbnail Bitmap
                if (data != null) {
                    // 没有指定特定存储路径的时候
                    Log.d(LOG_TAG,
                            "data is NOT null, file on default position.");

                    // 指定了存储路径的时候（intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);）
                    // Image captured and saved to fileUri specified in the
                    // Intent
                    Toast.makeText(this, "Image saved to:\n" + data.getData(),
                            Toast.LENGTH_LONG).show();

                    if (data.hasExtra("data")) {
                        Bitmap thumbnail = data.getParcelableExtra("data");
                        imageView.setImageBitmap(thumbnail);
                    }
                } else {

                    Log.d(LOG_TAG,
                            "data IS null, file saved on target position.");
                    // If there is no thumbnail image data, the image
                    // will have been stored in the target output URI.

                    // Resize the full image to fit in out image view.
                    int width = imageView.getWidth();
                    int height = imageView.getHeight();

                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(fileUri.getPath(), factoryOptions);

                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;

                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(imageWidth / width, imageHeight / height);

                    // Decode the image file into a Bitmap sized to fill the
                    // View
                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = scaleFactor;
                    factoryOptions.inPurgeable = true;

                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                            factoryOptions);

                    imageView.setImageBitmap(bitmap);
                }
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
    }
}

//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationClient;
//import com.amap.api.location.AMapLocationClientOption;
//import com.amap.api.location.AMapLocationListener;
//import com.amap.api.maps.AMap;
//import com.amap.api.maps.CameraUpdateFactory;
//import com.amap.api.maps.LocationSource;
//import com.amap.api.maps.MapView;
//import com.amap.api.maps.UiSettings;
//import com.amap.api.maps.model.LatLng;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {
//
//    //AMap是地图对象
//    private AMap aMap;
//    private MapView mapView;
//    //声明AMapLocationClient类对象，定位发起端
//    private AMapLocationClient mLocationClient = null;
//    //声明mLocationOption对象，定位参数
//    public AMapLocationClientOption mLocationOption = null;
//    //声明mListener对象，定位监听器
//    private OnLocationChangedListener mListener = null;
//    //标识，用于判断是否只显示一次定位信息和用户重新定位
//    private boolean isFirstLoc = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        //获取地图控件引用
//        mapView = (MapView) findViewById(R.id.map);
//        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
//        mapView.onCreate(savedInstanceState);
//        if (aMap == null) {
//            aMap = mapView.getMap();
//            //设置显示定位按钮 并且可以点击
//            UiSettings settings = aMap.getUiSettings();
//            aMap.setLocationSource(this);//设置了定位的监听
//            // 是否显示定位按钮
//            settings.setMyLocationButtonEnabled(true);
//            aMap.setMyLocationEnabled(true);//显示定位层并且可以触发定位,默认是flase
//        }
//        //开始定位
//        location();
//    }
//
//    private void location() {
//        //初始化定位
//        mLocationClient = new AMapLocationClient(getApplicationContext());
//        //设置定位回调监听
//        mLocationClient.setLocationListener(this);
//        //初始化定位参数
//        mLocationOption = new AMapLocationClientOption();
//        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        //设置是否返回地址信息（默认返回地址信息）
//        mLocationOption.setNeedAddress(true);
//        //设置是否只定位一次,默认为false
//        mLocationOption.setOnceLocation(false);
//        //设置是否强制刷新WIFI，默认为强制刷新
//        mLocationOption.setWifiActiveScan(true);
//        //设置是否允许模拟位置,默认为false，不允许模拟位置
//        mLocationOption.setMockEnable(false);
//        //设置定位间隔,单位毫秒,默认为2000ms
//        mLocationOption.setInterval(2000);
//        //给定位客户端对象设置定位参数
//        mLocationClient.setLocationOption(mLocationOption);
//        //启动定位
//        mLocationClient.startLocation();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//        mapView.onDestroy();
//        mLocationClient.stopLocation();//停止定位
//        mLocationClient.onDestroy();//销毁定位客户端。
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
//        mapView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
//        mapView.onPause();
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
//        mapView.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见官方定位类型表
//                aMapLocation.getLatitude();//获取纬度
//                aMapLocation.getLongitude();//获取经度
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码
//
//                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
//                if (isFirstLoc) {
//                    //设置缩放级别
//                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
//                    //将地图移动到定位点
//                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude())));
//                    //点击定位按钮 能够将地图的中心移动到定位点
//                    mListener.onLocationChanged(aMapLocation);
//                    //添加图钉
//                    //  aMap.addMarker(getMarkerOptions(amapLocation));
//                    //获取定位信息
//                    StringBuffer buffer = new StringBuffer();
//                    buffer.append(aMapLocation.getCountry() + ""
//                            + aMapLocation.getProvince() + ""
//                            + aMapLocation.getCity() + ""
//                            + aMapLocation.getProvince() + ""
//                            + aMapLocation.getDistrict() + ""
//                            + aMapLocation.getStreet() + ""
//                            + aMapLocation.getStreetNum());
//                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
//                    isFirstLoc = false;
//                }
//
//
//            } else {
//                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                Log.e("AmapError", "location Error, ErrCode:"
//                        + aMapLocation.getErrorCode() + ", errInfo:"
//                        + aMapLocation.getErrorInfo());
//                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//
//    @Override
//    public void activate(OnLocationChangedListener onLocationChangedListener) {
//        mListener = onLocationChangedListener;
//    }
//
//    @Override
//    public void deactivate() {
//        mListener = null;
//    }
//}