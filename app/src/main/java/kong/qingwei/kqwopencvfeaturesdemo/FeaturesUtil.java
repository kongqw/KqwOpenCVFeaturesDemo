package kong.qingwei.kqwopencvfeaturesdemo;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by kqw on 2016/8/19.
 * 提取图片特征的工具类
 */
public class FeaturesUtil {

    private Subscriber<Bitmap> mSubscriber;

    public FeaturesUtil(Subscriber<Bitmap> subscriber) {
        mSubscriber = subscriber;
    }

    /**
     * 高斯差分算法边缘检测
     *
     * @param bitmap 要检测的图片
     */
    public void differenceOfGaussian(Bitmap bitmap) {
        if (null != mSubscriber)
            Observable
                    .just(bitmap)
                    .map(new Func1<Bitmap, Bitmap>() {

                        @Override
                        public Bitmap call(Bitmap bitmap) {

                            Mat grayMat = new Mat();
                            Mat blur1 = new Mat();
                            Mat blur2 = new Mat();

                            // Bitmap转为Mat
                            Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                            Utils.bitmapToMat(bitmap, src);

                            // 原图置灰
                            Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_BGR2GRAY);

                            // 以两个不同的模糊半径对图像做模糊处理
                            Imgproc.GaussianBlur(grayMat, blur1, new Size(15, 15), 5);
                            Imgproc.GaussianBlur(grayMat, blur2, new Size(21, 21), 5);

                            // 将两幅模糊后的图像相减
                            Mat diff = new Mat();
                            Core.absdiff(blur1, blur2, diff);

                            // 反转二值阈值化
                            Core.multiply(diff, new Scalar(100), diff);
                            Imgproc.threshold(diff, diff, 50, 255, Imgproc.THRESH_BINARY_INV);

                            // Mat转Bitmap
                            Bitmap processedImage = Bitmap.createBitmap(grayMat.cols(), grayMat.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(diff, processedImage);

                            return processedImage;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSubscriber);
    }


    /**
     * Canny边缘检测算法
     *
     * @param bitmap 要检测的图片
     */
    public void canny(Bitmap bitmap) {
        if (null != mSubscriber)
            Observable
                    .just(bitmap)
                    .map(new Func1<Bitmap, Bitmap>() {

                        @Override
                        public Bitmap call(Bitmap bitmap) {

                            Mat grayMat = new Mat();
                            Mat cannyEdges = new Mat();

                            // Bitmap转为Mat
                            Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                            Utils.bitmapToMat(bitmap, src);

                            // 原图置灰
                            Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_BGR2GRAY);
                            // Canny边缘检测器检测图像边缘
                            Imgproc.Canny(grayMat, cannyEdges, 10, 100);

                            // Mat转Bitmap
                            Bitmap processedImage = Bitmap.createBitmap(cannyEdges.cols(), cannyEdges.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(cannyEdges, processedImage);

                            return processedImage;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSubscriber);
    }

    /**
     * Sobel滤波器
     *
     * @param bitmap 要检测的图片
     */
    public void sobel(Bitmap bitmap) {
        if (null != mSubscriber)
            Observable
                    .just(bitmap)
                    .map(new Func1<Bitmap, Bitmap>() {

                        @Override
                        public Bitmap call(Bitmap bitmap) {

                            Mat grayMat = new Mat();
                            Mat sobel = new Mat();
                            Mat grad_x = new Mat();
                            Mat grad_y = new Mat();
                            Mat abs_grad_x = new Mat();
                            Mat abs_grad_y = new Mat();

                            // Bitmap转为Mat
                            Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                            Utils.bitmapToMat(bitmap, src);
                            // 原图置灰
                            Imgproc.cvtColor(src, grayMat, Imgproc.COLOR_BGR2GRAY);

                            // 计算水平方向梯度
                            Imgproc.Sobel(grayMat, grad_x, CvType.CV_16S, 1, 0, 3, 1, 0);
                            // 计算垂直方向梯度
                            Imgproc.Sobel(grayMat, grad_y, CvType.CV_16S, 0, 1, 3, 1, 0);
                            // 计算两个方向上的梯度的绝对值
                            Core.convertScaleAbs(grad_x, abs_grad_x);
                            Core.convertScaleAbs(grad_y, abs_grad_y);
                            // 计算结果梯度
                            Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 1, sobel);

                            // Mat转Bitmap
                            Bitmap processedImage = Bitmap.createBitmap(sobel.cols(), sobel.rows(), Bitmap.Config.ARGB_8888);
                            Utils.matToBitmap(sobel, processedImage);

                            return processedImage;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSubscriber);
    }
}
