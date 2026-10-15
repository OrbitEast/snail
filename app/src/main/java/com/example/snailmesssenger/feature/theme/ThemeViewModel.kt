package com.example.snailmesssenger.feature.theme
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
class ThemeViewModel(app:Application):AndroidViewModel(app){
 private val prefs=app.getSharedPreferences("snail_theme_engine",0)
 private val _state=MutableStateFlow(load())
 val state=_state.asStateFlow()
 private fun load()=ThemeState(
  palette=runCatching{SnailPalette.valueOf(prefs.getString("palette","CLASSIC_BLUE")!!)}.getOrDefault(SnailPalette.CLASSIC_BLUE),
  nightMode=runCatching{NightMode.valueOf(prefs.getString("night","SYSTEM")!!)}.getOrDefault(NightMode.SYSTEM),
  customAccentHex=prefs.getString("accent","#2481CC")?: "#2481CC",
  messageTextSp=prefs.getFloat("textSize",16f),bubbleRadiusDp=prefs.getFloat("radius",16f),
  wallpaperHex=prefs.getString("wallpaper","#E7EBF0")?: "#E7EBF0",doodlePattern=prefs.getBoolean("doodles",true),
  wallpaperBlur=prefs.getBoolean("blur",false),wallpaperMotion=prefs.getBoolean("motion",false),wallpaperUri=prefs.getString("uri",null))
 private fun update(f:(ThemeState)->ThemeState){val s=f(_state.value);_state.value=s;prefs.edit().putString("palette",s.palette.name).putString("night",s.nightMode.name).putString("accent",s.customAccentHex).putFloat("textSize",s.messageTextSp).putFloat("radius",s.bubbleRadiusDp).putString("wallpaper",s.wallpaperHex).putBoolean("doodles",s.doodlePattern).putBoolean("blur",s.wallpaperBlur).putBoolean("motion",s.wallpaperMotion).putString("uri",s.wallpaperUri).apply()}
 private fun hex(v:String):String { val raw=v.trim().removePrefix("#"); return if(raw.length<=6 && raw.matches(Regex("(?i)[0-9a-f]*"))) "#"+raw.uppercase() else "#2481CC" }
 fun setPalette(v:SnailPalette)=update{it.copy(palette=v)}
 fun setNightMode(v:NightMode)=update{it.copy(nightMode=v)}
 fun setCustomAccent(v:String)=update{it.copy(customAccentHex=hex(v))}
 fun setMessageTextSp(v:Float)=update{it.copy(messageTextSp=v.coerceIn(12f,24f))}
 fun setBubbleRadiusDp(v:Float)=update{it.copy(bubbleRadiusDp=v.coerceIn(0f,18f))}
 fun setWallpaper(v:String)=update{it.copy(wallpaperHex=hex(v),wallpaperUri=null)}
 fun setWallpaperUri(v:String?)=update{it.copy(wallpaperUri=v)}
 fun setDoodles(v:Boolean)=update{it.copy(doodlePattern=v)}
 fun setWallpaperBlur(v:Boolean)=update{it.copy(wallpaperBlur=v)}
 fun setWallpaperMotion(v:Boolean)=update{it.copy(wallpaperMotion=v)}
}
