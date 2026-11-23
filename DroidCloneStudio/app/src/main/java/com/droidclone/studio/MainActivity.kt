package com.droidclone.studio

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg = Color(0xFF10111A)
private val Panel = Color(0xFF1B1D2A)
private val Accent = Color(0xFF8B7CFF)

class MainActivity : ComponentActivity() {
    private var onApkPicked: ((Uri?) -> Unit)? = null
    private val apkPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> onApkPicked?.invoke(uri) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var apkUri by remember { mutableStateOf<Uri?>(null) }
            var appName by remember { mutableStateOf("") }
            var packageId by remember { mutableStateOf("") }
            var version by remember { mutableStateOf("") }
            var error by remember { mutableStateOf<String?>(null) }
            var status by remember { mutableStateOf("Pilih sebuah file APK untuk memulai analisis.") }

            onApkPicked = { uri ->
                apkUri = uri
                error = null
                if (uri != null) {
                    try {
                        contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    } catch (_: Exception) { }
                    try {
                        val info = readApkInfo(uri)
                        appName = info.first
                        packageId = info.second
                        version = info.third
                        status = "Analisis APK berhasil. Identitas sumber berhasil dibaca."
                    } catch (e: Exception) {
                        error = e.message ?: "Tidak dapat membaca APK."
                        status = "Analisis gagal. Periksa apakah file merupakan APK yang valid."
                    }
                }
            }

            MaterialTheme(colorScheme = darkColorScheme(primary = Accent, background = Bg, surface = Panel)) {
                Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
                    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Android, null, tint = Accent, modifier = Modifier.size(38.dp))
                            Column {
                                Text("DroidClone Studio", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("APK utility · Local-first", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Mulai dari APK", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                                Text("Pilih satu file .apk dari penyimpanan perangkat. Versi alpha ini membaca metadata dasar tanpa mengunggah file.", color = Color.LightGray, fontSize = 13.sp)
                                Button(onClick = { apkPicker.launch(arrayOf("application/vnd.android.package-archive", "application/octet-stream")) }, modifier = Modifier.fillMaxWidth()) {
                                    Icon(Icons.Default.FolderOpen, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Pilih file APK")
                                }
                            }
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Informasi APK sumber", color = Color.White, fontWeight = FontWeight.SemiBold)
                                InfoLine("Status", status)
                                InfoLine("Nama aplikasi", appName.ifBlank { "Belum ada" })
                                InfoLine("Package ID", packageId.ifBlank { "Belum ada" })
                                InfoLine("Versi", version.ifBlank { "Belum ada" })
                                if (error != null) Text(error!!, color = Color(0xFFFF8A80), fontSize = 12.sp)
                            }
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Identitas clone", color = Color.White, fontWeight = FontWeight.SemiBold)
                                OutlinedTextField(value = appName, onValueChange = { appName = it }, label = { Text("Nama aplikasi baru") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                OutlinedTextField(value = packageId, onValueChange = { packageId = it }, label = { Text("Package ID baru") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                                Text("Editor ini menyiapkan konfigurasi. Rebuild dan penulisan ulang package belum diaktifkan pada alpha ini.", color = Color(0xFFFFD180), fontSize = 12.sp)
                            }
                        }
                        Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, null, tint = Accent)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Build & signing", color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                                Text("Tahap berikutnya: integrasi engine rebuild on-device, validasi manifest/resource, lalu APK signing v2/v3. Aplikasi tidak akan mengklaim clone berhasil sebelum APK hasilnya lolos pemeriksaan.", color = Color.LightGray, fontSize = 12.sp)
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF70D6A3), modifier = Modifier.size(16.dp))
                            Text("Offline · Tidak memakai API · APK biasa", color = Color.LightGray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    private fun readApkInfo(uri: Uri): Triple<String, String, String> {
        val temp = java.io.File(cacheDir, "inspect-${System.currentTimeMillis()}.apk")
        contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "File tidak dapat dibuka." }.copyTo(temp.outputStream())
        }
        try {
            val info: PackageInfo = if (Build.VERSION.SDK_INT >= 33) {
                packageManager.getPackageArchiveInfo(temp.absolutePath, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageArchiveInfo(temp.absolutePath, 0)
            } ?: throw IllegalArgumentException("Metadata APK tidak dapat dibaca.")
            val appInfo = info.applicationInfo ?: throw IllegalArgumentException("Informasi aplikasi tidak ditemukan.")
            appInfo.sourceDir = temp.absolutePath
            appInfo.publicSourceDir = temp.absolutePath
            val label = packageManager.getApplicationLabel(appInfo).toString()
            val ver = if (Build.VERSION.SDK_INT >= 28) info.longVersionCode.toString() else {
                @Suppress("DEPRECATION")
                info.versionCode.toString()
            }
            return Triple(label, info.packageName, ver)
        } finally { temp.delete() }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Text(label, color = Color.LightGray, fontSize = 12.sp)
        Spacer(Modifier.width(12.dp))
        Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
