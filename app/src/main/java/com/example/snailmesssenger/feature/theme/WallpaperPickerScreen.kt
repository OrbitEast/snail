package com.example.snailmesssenger.feature.theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
private val wallpaperColors=listOf("#E7EBF0","#F4F8F9","#D9EAF5","#CDE8DE","#F7E8CF","#F1DCE1","#DAD7F4","#B9D7E8","#A8C4A0","#34495E","#1C2733","#121212","#000000")
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun WallpaperPickerScreen(onBack:()->Unit,viewModel:ThemeViewModel=viewModel()){
 val s by viewModel.state.collectAsState()
 val context = LocalContext.current
 val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
  if (uri != null) {
   runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
   viewModel.setWallpaperUri(uri.toString())
  }
 }
 Scaffold(topBar={TopAppBar(title={Text("Chat Wallpaper")},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back")}})}){p->
 Column(Modifier.fillMaxSize().padding(p).padding(horizontal=18.dp)){
 Box(Modifier.fillMaxWidth().height(142.dp).background(parse(s.wallpaperHex)),contentAlignment=Alignment.Center){
  if (s.wallpaperUri != null) AsyncImage(model=s.wallpaperUri,contentDescription="Selected chat wallpaper",modifier=Modifier.fillMaxSize(),contentScale=ContentScale.Crop)
  if(s.doodlePattern) Text("◌  ✳  ⌁  ◇  ☼  ♧  ◌",color=Color.White.copy(alpha=.35f),style=MaterialTheme.typography.headlineMedium)
  Text("Snail chat preview",color=if(s.wallpaperHex in listOf("#1C2733","#121212","#000000","#34495E"))Color.White else Color(0xFF263238))
 }
 OutlinedButton(onClick={imagePicker.launch(arrayOf("image/*"))},modifier=Modifier.fillMaxWidth()){Text(if(s.wallpaperUri==null)"Choose image wallpaper" else "Change image wallpaper")}
 Spacer(Modifier.height(12.dp));Text("Solid colors",style=MaterialTheme.typography.titleMedium)
 LazyVerticalGrid(columns=GridCells.Fixed(4),modifier=Modifier.fillMaxWidth().height(168.dp),horizontalArrangement=Arrangement.spacedBy(12.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 items(wallpaperColors){hex->Box(Modifier.size(56.dp).background(parse(hex),CircleShape).clickable{viewModel.setWallpaper(hex)},contentAlignment=Alignment.Center){if(s.wallpaperHex.equals(hex,true))Icon(Icons.Default.Check,"Selected",tint=if(hex in listOf("#000000","#121212","#1C2733","#34495E"))Color.White else Color.Black)}}
 }
 SettingToggle("Doodle pattern","Faint decorative overlay",s.doodlePattern,viewModel::setDoodles)
 SettingToggle("Blur wallpaper","Softens busy backgrounds",s.wallpaperBlur,viewModel::setWallpaperBlur)
 SettingToggle("Motion effect","Subtle parallax placeholder",s.wallpaperMotion,viewModel::setWallpaperMotion)
 }
 }
}
@Composable private fun SettingToggle(title:String,subtitle:String,checked:Boolean,onChange:(Boolean)->Unit){Row(Modifier.fillMaxWidth().padding(vertical=7.dp),verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(title);Text(subtitle,style=MaterialTheme.typography.bodySmall,color=MaterialTheme.colorScheme.onSurfaceVariant)};Switch(checked=checked,onCheckedChange=onChange)}}
private fun parse(hex:String)=runCatching{Color(android.graphics.Color.parseColor(hex))}.getOrDefault(Color.LightGray)
