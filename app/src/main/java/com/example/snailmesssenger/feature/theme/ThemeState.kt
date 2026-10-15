package com.example.snailmesssenger.feature.theme
import androidx.compose.runtime.Immutable
enum class SnailPalette(val label:String,val primaryHex:Long,val lightBackgroundHex:Long,val darkBackgroundHex:Long,val darkSurfaceHex:Long){
 CLASSIC_BLUE("Classic Blue",0xFF2481CC,0xFFFFFFFF,0xFF1C2733,0xFF243142),
 DAY_CYAN("Day / Cyan",0xFF0088CC,0xFFF4FAFD,0xFF152B37,0xFF203746),
 NIGHT("Night",0xFF2481CC,0xFFFFFFFF,0xFF1C2733,0xFF243142),
 AMOLED("Pitch Black",0xFF2481CC,0xFFFFFFFF,0xFF000000,0xFF121212),
 MONOCHROME("Custom Accent",0xFF616B75,0xFFFFFFFF,0xFF17191C,0xFF24272B)
}
enum class NightMode(val label:String){SYSTEM("System Default"),LIGHT("Always Light"),DARK("Always Dark")}
@Immutable data class ThemeState(
 val palette:SnailPalette=SnailPalette.CLASSIC_BLUE,val nightMode:NightMode=NightMode.SYSTEM,
 val customAccentHex:String="#2481CC",val messageTextSp:Float=16f,val bubbleRadiusDp:Float=16f,
 val wallpaperHex:String="#E7EBF0",val doodlePattern:Boolean=true,val wallpaperBlur:Boolean=false,
 val wallpaperMotion:Boolean=false,val wallpaperUri:String?=null
)
