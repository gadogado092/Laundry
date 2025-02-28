package amat.laundry.ui.screen.bill

import amat.laundry.R
import amat.laundry.currencyFormatterStringViewZero
import amat.laundry.data.entity.BillEntity
import amat.laundry.databinding.ActivityBillBinding
import amat.laundry.di.Injection
import amat.laundry.getSerializable
import amat.laundry.sendWhatsApp
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class BillActivityXml : AppCompatActivity() {
    companion object {
        const val EXTRA_BILL = "extra_bill"
        const val TAG = "BillActivity"
        const val permissionCode = 123
        const val permissionCodeOS33 = 1234
    }

    private var _binding: ActivityBillBinding? = null
    private val binding get() = _binding!!

    var billEntity = BillEntity()
    private var type = ""

    private val myViewModel: BillViewModel by viewModels {
        BillViewModelFactory(Injection.provideUserRepository(this))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBillBinding.inflate(layoutInflater)
        setContentView(binding.root)

        title = getString(R.string.bill)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        billEntity = getSerializable(this, "object", BillEntity::class.java)
        binding.textKostName.text =
            if (billEntity.kostName.isEmpty()) "Kelola Kost" else billEntity.kostName
        binding.textDate.text = billEntity.createAt
        binding.textNominal.text = currencyFormatterStringViewZero(billEntity.nominal)
        binding.textNote.text = billEntity.note


        if (billEntity.tenantNumberPhone == "") {
            binding.linearLayoutSend.visibility = View.GONE
        }

        binding.buttonDownload.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                downloadImageOs33()
            } else {
                methodRequiresPermission("download")
            }
        }

        binding.buttonShare.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                shareImageOs33()
            } else {
                methodRequiresPermission("share")
            }
        }

        binding.buttonTextSms.setOnClickListener {
            val message =
                "Tanggal ${billEntity.createAt} \nNominal ${billEntity.nominal} \nKeterangan ${billEntity.note}"
            val messageReport =
                "Assalamualaikum...\nSelamat Pagi, Siang, Sore atau Malam...\n\nInfo Laporan $message"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("smsto:${billEntity.tenantNumberPhone}")
                putExtra("sms_body", messageReport)
            }
            if (intent.resolveActivity(applicationContext.packageManager) != null) {
                startActivity(intent)
            }
        }

        binding.buttonTextWa.setOnClickListener {
            val message =
                "Tanggal ${billEntity.createAt} \nNominal ${billEntity.nominal} \nKeterangan ${billEntity.note}"
            val messageReport =
                "Assalamualaikum...\nSelamat Pagi, Siang, Sore atau Malam...\n\nInfo Laporan $message"
            sendWhatsApp(
                it.context,
                billEntity.tenantNumberPhone,
                messageReport,
                myViewModel.typeWa.value
            )
        }
    }

    private fun takeScreenshot(rootView: View): Bitmap {
        val bitmap =
            Bitmap.createBitmap(rootView.width, rootView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(ContextCompat.getColor(rootView.context, R.color.white))
        rootView.draw(canvas)
        return bitmap
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        try {
            val filename = "${System.currentTimeMillis()}.jpg"
            var imageUri: Uri? = null
            var fos: OutputStream? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.contentResolver?.also { resolver ->
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    imageUri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = imageUri?.let { resolver.openOutputStream(it) }
                }
            } else {
                val imagesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val image = File(imagesDir, filename)
                imageUri = Uri.fromFile(image)
                fos = FileOutputStream(image)
            }
            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                Toast.makeText(this, "Bill Success saved to Gallery", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            showSnackBar("Gagal... ${e.message.toString()}")
        }
    }

    private fun saveMediaToStorageUri(bitmap: Bitmap): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        var imageUri: Uri? = null
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                imageUri =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            imageUri = Uri.fromFile(image)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return imageUri
    }

    private fun share(bitmap: Bitmap) {
        try {
            val uri = saveMediaToStorageUri(bitmap)
            if (uri == null) {
                Toast.makeText(this, "Can't Share... Try To Download", Toast.LENGTH_SHORT).show()
            } else {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/jpg"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Bukti Pembayaran")
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(Intent.createChooser(shareIntent, "Bukti Pembayaran"))
            }
        } catch (e: Exception) {
            showSnackBar("Gagal... ${e.message.toString()}")
        }
    }

    private fun downloadImageOs33() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    permissionCodeOS33
                )
            }
        } else {
            val screenshot = takeScreenshot(binding.layoutBill)
            saveMediaToStorage(screenshot)
        }
    }

    private fun shareImageOs33() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    permissionCodeOS33
                )
            }
        } else {

            try {
                val screenshot = takeScreenshot(binding.layoutBill)
                val uri = saveMediaToStorageUri(screenshot)
                if (uri == null) {
                    Toast.makeText(this, "Can't Share... Try To Download", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "image/jpg"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Bukti Pembayaran")
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(shareIntent, "Bukti pembayaran"))
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                showSnackBar("Gagal Bagikan... ${e.message.toString()}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionCode) {
            // Checking whether user granted the permission or not.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (type == "share") {
                    val b = takeScreenshot(binding.layoutBill)
                    share(b)
                } else if (type == "download") {
                    val b = takeScreenshot(binding.layoutBill)
                    saveMediaToStorage(b)
                }
            } else {
                showSnackBar("proses gagal karena izin tidak diberikan")
            }
        }

        if (requestCode == permissionCodeOS33) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSnackBar("Izin akses media diberikan")

            } else {
                showSnackBar("Proses gagal karena akses media tidak diberikan... Pengaturan/App Info->Permission")
            }
        }
    }

    @AfterPermissionGranted(permissionCode)
    private fun methodRequiresPermission(type: String) {
        val perms =
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            if (type == "share") {
                val b = takeScreenshot(binding.layoutBill)
                share(b)
            } else if (type == "download") {
                val b = takeScreenshot(binding.layoutBill)
                saveMediaToStorage(b)
            }
        } else {
            this.type = type
            EasyPermissions.requestPermissions(
                this,
                "Membutuhkan izin agar gambar dapat diproses",
                permissionCode,
                *perms
            )
        }
    }

    private fun showSnackBar(message: String) {
        binding.let {
            Snackbar.make(
                it.root,
                message,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}