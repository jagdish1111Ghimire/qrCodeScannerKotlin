package info.jagdish.qrcodescanner

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import info.jagdish.qrcodescanner.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.internal.BarcodeScannerImpl
import com.google.mlkit.vision.common.InputImage

class MainActivity : AppCompatActivity() {
   private lateinit var binding
   :ActivityMainBinding
   val options = BarcodeScannerOptions.Builder()
       .setBarcodeFormats(
           Barcode.FORMAT_QR_CODE,
           Barcode.FORMAT_AZTEC
       )
       .build()

private val REQUEST_IMAGE_CAPT = 1
    private var  imageBitmap :Bitmap? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater) //initializing the binding class
        setContentView(binding.root)
        binding.apply {
            btnToCapture.setOnClickListener {
                takeImage()
                txt.text = ""

            }
            btnToDetect.setOnClickListener {
                detectImage()
            }

        }

    }

    private fun detectImage() {
        if(imageBitmap != null){
            val image = InputImage.fromBitmap(imageBitmap!!,0)
            val scanner = BarcodeScanning.getClient(options)
            scanner.process(image).addOnSuccessListener {
                barcodes ->

                if(barcodes.toString() == "[]"){
                    Toast.makeText(this,"Nothing to scan",Toast.LENGTH_LONG).show()
                }
                for (barcode in barcodes)
                {
                    when (barcode.valueType){
                        Barcode.TYPE_WIFI -> {
                            val ssid = barcode.wifi!!.ssid
                            val password = barcode.wifi!!.password
                            val type = barcode.wifi!!.encryptionType

                            binding.txt.text = "$ssid \n $password \n $type"
                            this.makeText("$ssid \n" +
                                    " $password \n" +
                                    " $type")


                    }
                        Barcode.TYPE_URL ->{
                            binding.txt .text= "${barcode.url!!.title} \n ${barcode.url!!.url}"
                            this.makeText("${barcode.url!!.title} ${barcode.url!!.url}")
                        }
                    }
                }
            }
        }
        else{
            Toast.makeText(this,"Please select an Image",Toast.LENGTH_LONG).show()
        }
    }

    private fun takeImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(intent,REQUEST_IMAGE_CAPT)
        }
        catch (e:Exception){}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_IMAGE_CAPT && resultCode == RESULT_OK)
        {
            val extras:Bundle?=data?.extras
            imageBitmap = extras?.get("data") as Bitmap
            if(imageBitmap != null){
                binding.img.setImageBitmap(imageBitmap)
            }

        }
    }

    private fun Context.makeText(mesaage :String){
        Toast.makeText(this,mesaage,Toast.LENGTH_LONG).show()
    }

}