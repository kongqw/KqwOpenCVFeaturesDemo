package kong.qingwei.kqwopencvfeaturesdemo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import rx.Subscriber;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ImageView mImageView;
    private Bitmap mSelectImage;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV Manager已安装，可以学习OpenCV啦。");
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    private FeaturesUtil mFeaturesUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImageView = (ImageView) findViewById(R.id.image_view);

        // 图片特征提取的工具类
        mFeaturesUtil = new FeaturesUtil(new Subscriber<Bitmap>() {
            @Override
            public void onCompleted() {
                // 图片处理完成
                dismissProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                // 图片处理异常
                dismissProgressDialog();
            }

            @Override
            public void onNext(Bitmap bitmap) {
                // 获取到处理后的图片
                mImageView.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11, this, mOpenCVCallBack);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_open_gallery) {
            // 选择图片
            Intent intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://media/internal/images/media"));
            startActivityForResult(intent, 0);
            return true;
        } else if (id == R.id.action_difference_of_gaussian) {
            // 高斯差分技术实现图像边缘检测
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("正在使用高斯差分实现图像边缘检测...");
            // 高斯差分技术检测图像边缘
            mFeaturesUtil.differenceOfGaussian(mSelectImage);
            return true;
        } else if (id == R.id.action_canny) {
            // Canny边缘检测器
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("正在使用Canny边缘检测器检测图像边缘...");
            // Canny边缘检测器检测图像边缘
            mFeaturesUtil.canny(mSelectImage);
            return true;
        } else if (id == R.id.action_sobel) {
            // Sobel滤波器
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("正在使用Sobel滤波器器检测图像边缘...");
            // Sobel滤波器检测图像边缘
            mFeaturesUtil.sobel(mSelectImage);
            return true;
        } else if (id == R.id.action_harris) {
            // Harris角点检测
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("正在使用Harris角点检测...");
            // Harris角点检测
            mFeaturesUtil.harris(mSelectImage);
            return true;
        } else if (id == R.id.action_hough_lines) {
            // 霍夫直线
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("霍夫直线...");
            // 霍夫直线
            mFeaturesUtil.houghLines(mSelectImage);
            return true;
        } else if (id == R.id.action_hough_circles) {
            // 霍夫圆
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("霍夫圆...");
            // 霍夫圆
            mFeaturesUtil.houghCircles(mSelectImage);
            return true;
        } else if (id == R.id.action_find_contours) {
            // 找出轮廓
            if (null == mSelectImage) {
                Snackbar.make(mImageView, "请先选择一张图片", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            showProgressDialog("找出轮廓...");
            // 找出轮廓
            mFeaturesUtil.findContours(mSelectImage);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (0 == requestCode && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                // 加速图像的载入
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                mSelectImage = BitmapFactory.decodeFile(picturePath, options);


                mImageView.setImageBitmap(mSelectImage);
            }


//            // 获取方向信息
//            int orientation = 0;
//            try {
//                ExifInterface imgParams = new ExifInterface(picturePath);
//                orientation = imgParams.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            //Snackbar.make(mImageView, "orientation = " + orientation, Snackbar.LENGTH_SHORT).show();

//            // 旋转图像
//            Matrix rotate90 = new Matrix();
//            rotate90.postRotate(orientation);
//            rotate90
        }
    }
}
