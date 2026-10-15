package com.example.snailmesssenger.feature.theme
import androidx.compose.runtime.Immutable
enum class SnailPalette(val label:String,val primaryHex:Long,val lightBackgroundHex:Long,val darkBackgroundHex:Long,val darkSurfaceHex:Long){
 CLASSIC_BLUE("Telegram Blue",0xFF229ED9,0xFFF0F2F5,0xFF0F1826,0xFF17212B),
 DAY_CYAN("Day / Cyan",0xFF0088CC,0xFFF4FAFD,0xFF152B37,0xFF203746),
 NIGHT("Telegram Dark",0xFF229ED9,0xFFF0F2F5,0xFF0F1826,0xFF17212B),
 AMOLED("Pitch Black",0xFF229ED9,0xFFFFFFFF,0xFF000000,0xFF121212),
 MONOCHROME("Custom Accent",0xFF616B75,0xFFF0F2F5,0xFF17191C,0xFF24272B)
}
enum class NightMode(val label:String){SYSTEM("System Default"),LIGHT("Always Light"),DARK("Always Dark")}
@Immutable data class ThemeState(
 val palette:SnailPalette=SnailPalette.CLASSIC_BLUE,val nightMode:NightMode=NightMode.DARK,
 val customAccentHex:String="#229ED9",val messageTextSp:Float=15.5f,val bubbleRadiusDp:Float=14f,
 val wallpaperHex:String="#0F1826",val doodlePattern:Boolean=true,val wallpaperBlur:Boolean=false,
 val wallpaperMotion:Boolean=false,val wallpaperUri:String?=null
)
