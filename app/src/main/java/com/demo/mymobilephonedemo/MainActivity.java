package com.demo.mymobilephonedemo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private CommonProgressDialog commonProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //进入这里之后，获取版本号是否更新
        int versionCode = AppUtils.getVersionCode(this);
        getUpdate(versionCode);
    }

    //版本更新
    private void getUpdate(int versionCode) {
        //一般情况下是在这里拿本地的版本号和服务器的版本号进行比较的，我没有服务器就模拟拿数据了
        String  newVersion="2.1";
        String content ="\n"+
        "就不告诉你我们更新了什么-。-\n" +
                "\n" +
                "----------万能的分割线-----------\n" +
                "\n" +
                "1.新产品上线了，界面全新改版\n" +
                "2.修复了若干bug，还杀了一个程序员祭天 \n";//更新内容
         String url = "http://openbox.mobilem.360.cn/index/d/sid/3429345"; //安装包下载地址
         double newVersionCode = Double.parseDouble(newVersion);
         int  cc= (int)(newVersionCode);
         if(cc!=versionCode){
             if(cc>versionCode){
                 //版本号不同，这时候我们需要开始弹窗了
                 //需要强制更新的在这里选择不同的弹窗方式
                 ShowDialog(url,content);
             }
         }
    }

    /**
     * 升级版本
     * @param url
     * @param content
     */
    private void ShowDialog(final String url, String content) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage(content);
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               //这里进行更新的操作
                dialogInterface.dismiss();
                //提示版本更新的信息消失之后，开始在弹出本地下载的进度条。这里自定义一个dialog
                commonProgressDialog = new CommonProgressDialog(MainActivity.this);
                commonProgressDialog.setCanceledOnTouchOutside(false);
                commonProgressDialog.setTitle("正在下载");
                commonProgressDialog.setCustomTitle(
                LayoutInflater.from(MainActivity.this).inflate(R.layout.title_dialog,null)
                );
                commonProgressDialog.setIndeterminate(true);
                commonProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                commonProgressDialog.setCancelable(true);

                //downFile(URLData.DOWNLOAD_URL);
              final DownloadTask downloadTask = new DownloadTask(MainActivity.this);
                downloadTask.execute(url);
                commonProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        downloadTask.cancel(true);
                    }
                });

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }

    // 下载存储的文件名
    private static final String DOWNLOAD_NAME = "channelWe";

    /**
     * 下载应用
     *
     * @author Administrator
     */
    class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            File file = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error
                // report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }
                int fileLength = connection.getContentLength();
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    file = new File(Environment.getExternalStorageDirectory(),
                            DOWNLOAD_NAME);

                    if (!file.exists()) {
                        // 判断父文件夹是否存在
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }
                    }

                } else {
                    Toast.makeText(MainActivity.this, "sd卡未挂载",
                            Toast.LENGTH_LONG).show();
                }
                input = connection.getInputStream();
                output = new FileOutputStream(file);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);

                }
            } catch (Exception e) {
                System.out.println(e.toString());
                return e.toString();

            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            commonProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            commonProgressDialog.setIndeterminate(false);
            commonProgressDialog.setMax(100);
            commonProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            commonProgressDialog.dismiss();
            if (result != null) {

                // 申请多个权限。
                AndPermission.with(MainActivity.this)
                        .requestCode(REQUEST_CODE_PERMISSION_SD)
                        .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框，避免用户勾选不再提示。
                        .rationale(rationaleListener
                        )
                        .send();


                Toast.makeText(context, "您未打开SD卡权限" + result, Toast.LENGTH_LONG).show();
            } else {
                // Toast.makeText(context, "File downloaded",
                // Toast.LENGTH_SHORT)
                // .show();
                update();
            }

        }
    }





    private static final int REQUEST_CODE_PERMISSION_SD = 101;

    private static final int REQUEST_CODE_SETTING = 300;
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            // 这里使用自定义对话框，如果不想自定义，用AndPermission默认对话框：
            // AndPermission.rationaleDialog(Context, Rationale).show();

            // 自定义对话框。
            com.yanzhenjie.alertdialog.AlertDialog.build(MainActivity.this)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_rationale)
                    .setPositiveButton(R.string.btn_dialog_yes_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })

                    .setNegativeButton(R.string.btn_dialog_no_permission, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    })
                    .show();
        }
    };
    //----------------------------------SD权限----------------------------------//


    @PermissionYes(REQUEST_CODE_PERMISSION_SD)
    private void getMultiYes(List<String> grantedPermissions) {
        Toast.makeText(this, R.string.message_post_succeed, Toast.LENGTH_SHORT).show();
    }

    @PermissionNo(REQUEST_CODE_PERMISSION_SD)
    private void getMultiNo(List<String> deniedPermissions) {
        Toast.makeText(this, R.string.message_post_failed, Toast.LENGTH_SHORT).show();

        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                    .setTitle(R.string.title_dialog)
                    .setMessage(R.string.message_permission_failed)
                    .setPositiveButton(R.string.btn_dialog_yes_permission)
                    .setNegativeButton(R.string.btn_dialog_no_permission, null)
                    .show();

            // 更多自定dialog，请看上面。
        }
    }

    //----------------------------------权限回调处理----------------------------------//

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        /**
         * 转给AndPermission分析结果。
         *
         * @param object     要接受结果的Activity、Fragment。
         * @param requestCode  请求码。
         * @param permissions  权限数组，一个或者多个。
         * @param grantResults 请求结果。
         */
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SETTING: {
                Toast.makeText(this, R.string.message_setting_back, Toast.LENGTH_LONG).show();
                //设置成功，再次请求更新
                getUpdate(AppUtils.getVersionCode(MainActivity.this));
                break;
            }
        }
    }


    private void update() {
        //安装应用
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(MainActivity.this, "com.demo.mymobilephonedemo.fileprovider",new File(Environment
                      .getExternalStorageDirectory(), DOWNLOAD_NAME));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(Environment
                      .getExternalStorageDirectory(), DOWNLOAD_NAME)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        MainActivity.this.startActivity(intent);
    }
}
