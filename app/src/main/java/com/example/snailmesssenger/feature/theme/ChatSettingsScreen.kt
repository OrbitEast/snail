package com.example.snailmesssenger.feature.theme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun ChatSettingsScreen(onBack:()->Unit,onOpenWallpaper:()->Unit,viewModel:ThemeViewModel=viewModel()){
 val s by viewModel.state.collectAsState()
 Scaffold(topBar={TopAppBar(title={Text("Chat Settings")},navigationIcon={IconButton(onClick=onBack){Icon(Icons.AutoMirrored.Filled.ArrowBack,"Back")}})}){p->
 Column(Modifier.fillMaxSize().padding(p).verticalScroll(rememberScrollState()).padding(18.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("Live preview",style=MaterialTheme.typography.titleMedium,fontWeight=FontWeight.SemiBold)
 Card(colors=CardDefaults.cardColors(containerColor=hexColor(s.wallpaperHex)),modifier=Modifier.fillMaxWidth()){
 Column(Modifier.fillMaxWidth().padding(14.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
 Text("Maya: Hey! How's your day?",fontSize=s.messageTextSp.sp,color=Color(0xFF202830),modifier=Modifier.align(Alignment.Start).background(Color.White,RoundedCornerShape(s.bubbleRadiusDp.dp)).padding(12.dp))
 Text("Going well, thanks 👋",fontSize=s.messageTextSp.sp,color=Color(0xFF202830),modifier=Modifier.align(Alignment.End).background(Color(0xFFCDECCF),RoundedCornerShape(s.bubbleRadiusDp.dp)).padding(12.dp))
 }}
 Text("Color palette",style=MaterialTheme.typography.titleMedium)
 Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(14.dp)){
 SnailPalette.entries.forEach{palette->Column(horizontalAlignment=Alignment.CenterHorizontally){
 Surface(onClick={viewModel.setPalette(palette)},modifier=Modifier.size(52.dp),shape=CircleShape,color=Color(palette.primaryHex),contentColor=Color.White,border=if(s.palette==palette)BorderStroke(3.dp,MaterialTheme.colorScheme.onSurface)else null){Box(contentAlignment=Alignment.Center){if(s.palette==palette)Icon(Icons.Default.Check,"Selected")}}
 Text(palette.label,fontSize=10.sp,modifier=Modifier.padding(top=4.dp))
 }}
 }
 if(s.palette==SnailPalette.MONOCHROME) OutlinedTextField(value=s.customAccentHex,onValueChange=viewModel::setCustomAccent,label={Text("Accent HEX (#RRGGBB)")},singleLine=true)
 Text("Night mode",style=MaterialTheme.typography.titleMedium)
 NightMode.entries.forEach{mode->Row(verticalAlignment=Alignment.CenterVertically){RadioButton(selected=s.nightMode==mode,onClick={viewModel.setNightMode(mode)});Text(mode.label)}}
 Text("Message text size: "+s.messageTextSp.toInt()+"sp")
 Slider(value=s.messageTextSp,onValueChange=viewModel::setMessageTextSp,valueRange=12f..24f)
 Text("Bubble corner radius: "+s.bubbleRadiusDp.toInt()+"dp")
 Slider(value=s.bubbleRadiusDp,onValueChange=viewModel::setBubbleRadiusDp,valueRange=0f..18f)
 OutlinedButton(onClick=onOpenWallpaper,modifier=Modifier.fillMaxWidth()){Text("Customize chat wallpaper")}
 }
 }
}
private fun hexColor(hex:String)=runCatching{Color(android.graphics.Color.parseColor(hex))}.getOrDefault(Color(0xFFE7EBF0))
