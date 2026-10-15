package com.example.snailmesssenger.feature.media

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val MediaBlue = Color(0xFF2481CC)

@Composable
fun GalleryThumbnailItem(media: MediaAsset, selectionOrder: Int?, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val selected = selectionOrder != null
    val scale by animateFloatAsState(if (selected) 0.94f else 1f, animationSpec = spring(dampingRatio = 0.48f, stiffness = 520f), label = "thumbnail-selection-scale")
    Box(modifier = modifier.scale(scale).clip(MaterialTheme.shapes.medium).background(Color(media.tintArgb)).clickable(onClick = onClick)) {
        if (media.uri != null) {
            AsyncImage(model = media.uri, contentDescription = media.title, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(Color(media.tintArgb)), contentAlignment = Alignment.Center) {
                Text(media.title.substringBefore(" "), color = Color.White, fontSize = 11.sp)
            }
        }
        if (media.kind == MediaKind.VIDEO) {
            Surface(modifier = Modifier.align(Alignment.BottomStart).padding(6.dp), color = Color.Black.copy(alpha = 0.62f), shape = MaterialTheme.shapes.small) {
                Row(Modifier.padding(horizontal = 5.dp, vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Text(media.durationLabel.orEmpty(), color = Color.White, fontSize = 10.sp)
                }
            }
        }
        if (selected) {
            Surface(modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(25.dp), shape = CircleShape, color = MediaBlue, shadowElevation = 2.dp) {
                Box(contentAlignment = Alignment.Center) {
                    Text(selectionOrder.toString(), color = Color.White, fontSize = 12.sp)
                }
            }
        } else {
            Surface(modifier = Modifier.align(Alignment.TopEnd).padding(7.dp).size(21.dp), shape = CircleShape, color = Color.Black.copy(alpha = 0.18f)) {}
        }
    }
}
