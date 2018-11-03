package apps.tucancha.View.Activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import apps.tucancha.R;

public class ReceiverActivity extends AppCompatActivity implements TextDecodingCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_receiver);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        if (type.startsWith("image/")) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                int[] intArray = new int[   bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
                BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

                QRCodeReader qrCodeReader = new QRCodeReader();

                Result result = qrCodeReader.decode(binaryBitmap);


                Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();

                Intent intentEnvio = new Intent(ReceiverActivity.this,MainActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString(MainActivity.CLAVE_JSON,result.getText());

                intentEnvio.putExtras(bundle);

                startActivity(intent);



            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "LA IMAGEN NO ES VALIDA", Toast.LENGTH_SHORT).show();

                Intent intentEnvio = new Intent(ReceiverActivity.this,MainActivity.class);

                startActivity(intentEnvio);
                finish();
            }
        }


    }

    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        if (result != null) {
            if (result.isDecoded()) {
                Intent intent = new Intent(ReceiverActivity.this, MainActivity.class);

                Bundle bundle = new Bundle();

                bundle.putString(MainActivity.CLAVE_JSON, result.getMessage());

                intent.putExtras(bundle);

                startActivity(intent);

            } else {
                Toast.makeText(this, "LA IMAGEN NO ES VALIDA", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ReceiverActivity.this, MainActivity.class);

                startActivity(intent);
            }


        }

    }
}
