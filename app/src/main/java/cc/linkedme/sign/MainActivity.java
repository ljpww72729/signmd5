package cc.linkedme.sign;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity {

    EditText packageName;
    TextView appMd5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        packageName = findViewById(R.id.package_name);
        Button checkMd5 = findViewById(R.id.check_md5);
        Button copy = findViewById(R.id.copy);
        appMd5 = findViewById(R.id.app_md5);
        checkMd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSign(MainActivity.this);
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cbm.setPrimaryClip(ClipData.newPlainText("md5", appMd5.getText().toString()));
                Toast.makeText(MainActivity.this, "已复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getSign(Context ctx) {
        try {
            Signature[] signs;
            PackageInfo packageInfo;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo = ctx.getPackageManager().getPackageInfo(packageName.getText().toString(),
                        PackageManager.GET_SIGNING_CERTIFICATES);
                signs = packageInfo.signingInfo.getApkContentsSigners();
            } else {
                packageInfo = ctx.getPackageManager().getPackageInfo(packageName.getText().toString(),
                        PackageManager.GET_SIGNATURES);
                signs = packageInfo.signatures;
            }
            if (signs != null && signs.length > 0) {
                Signature sign = signs[0];
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(sign.toByteArray());
                byte[] digest = md5.digest();
                String md5Str = toHexString(digest);
                appMd5.setText(md5Str);
            } else {
                Toast.makeText(ctx, "未获取到应用签名", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toHexString(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length + bytes.length);
        for (byte b : bytes) {
            if (((int) b & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString((int) b & 0xff, 16));
        }
        return buf.toString();
    }

}
