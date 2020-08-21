package com.vinstudio.demodowloaddropbox

import android.Manifest
import android.R.attr.password
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadFromDropBoxUrl()
        if (unpackZip( assets.toString(),"abc")==true) {
            Toast.makeText(applicationContext, "Unzip successfully.", Toast.LENGTH_LONG).show()
        }else{
            println("------------------------")
        }
    }

    fun downloadFromDropBoxUrl() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permissions, 1000)
            } else {
                startDownloading()
            }
        } else {
            startDownloading()
        }
    }

    fun startDownloading() {
        val url = "https://www.dropbox.com/s/y6s0j36t8xcivkw/assets.zip?dl=1"
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("Download")
        request.setDescription("Dowloading file...")
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"History.zip")
        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            1000 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownloading()
                } else {
                    Toast.makeText(this, "Permission denied ...", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun unpackZip(path: String, zipname: String): Boolean {
        val `is`: InputStream
        val zis: ZipInputStream
        try {
            `is` = FileInputStream(path + zipname)
            zis = ZipInputStream(BufferedInputStream(`is`))
            var ze: ZipEntry
            while (zis.nextEntry.also { ze = it } != null) {
                val baos = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var count: Int
                val filename = ze.name
                val fout = FileOutputStream(path + filename)

                // reading and writing
                while (zis.read(buffer).also { count = it } != -1) {
                    baos.write(buffer, 0, count)
                    val bytes = baos.toByteArray()
                    fout.write(bytes)
                    baos.reset()
                }
                fout.close()
                zis.closeEntry()
            }
            zis.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }
}