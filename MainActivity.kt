package com.carter.toolkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.random.Random

private val Bg = Color(0xFF08090D)
private val Panel = Color(0xFF171920)
private val Red = Color(0xFFFF3048)
private val Muted = Color(0xFF9A9DA8)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ToolkitApp() }
    }
}

@Composable
fun ToolkitApp() {
    var page by remember { mutableStateOf("Home") }
    var sensitivity by remember { mutableFloatStateOf(65f) }
    var crosshairSize by remember { mutableFloatStateOf(18f) }
    var difficulty by remember { mutableStateOf("Normal") }
    var score by remember { mutableIntStateOf(0) }
    var hits by remember { mutableIntStateOf(0) }
    var misses by remember { mutableIntStateOf(0) }
    var targetX by remember { mutableFloatStateOf(.5f) }
    var targetY by remember { mutableFloatStateOf(.5f) }
    var running by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Red, background = Bg, surface = Panel,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Column(
            Modifier.fillMaxSize().background(Bg).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("CART3R", color = Red, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("FORTNITE TOOLKIT", color = Muted, fontSize = 11.sp, letterSpacing = 2.sp)
                }
                Text("v1.0.0", color = Muted, fontSize = 12.sp)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Home", "Aim", "Settings", "Stats").forEach { item ->
                    Button(
                        onClick = { page = item },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (page == item) Red else Panel
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) { Text(item, fontSize = 12.sp) }
                }
            }
            when (page) {
                "Home" -> {
                    Text("TRAINING DASHBOARD", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    FeatureCard("ADVANCED AIM TRAINER", "Moving targets, hit tracking and difficulty levels") { page = "Aim" }
                    FeatureCard("SENSITIVITY LAB", "Adjust and note your preferred practice sensitivity") { page = "Settings" }
                    FeatureCard("CROSSHAIR STUDIO", "Preview a customizable practice crosshair") { page = "Settings" }
                    FeatureCard("PERFORMANCE TRACKER", "Review your hits, misses and accuracy") { page = "Stats" }
                    Text("Practice companion only — does not modify Fortnite or interact with live matches.",
                        color = Muted, fontSize = 11.sp)
                }
                "Aim" -> {
                    Text("AIM TRAINER", color = Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Score  $score      Hits  $hits      Misses  $misses", color = Color.White)
                    Text("Difficulty", color = Muted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Easy", "Normal", "Hard").forEach {
                            FilterChip(selected = difficulty == it, onClick = { difficulty = it },
                                label = { Text(it) })
                        }
                    }
                    Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xFF11131A), RoundedCornerShape(18.dp))) {
                        Canvas(Modifier.fillMaxSize().clickable {
                            if (running) {
                                misses++
                            }
                        }) {
                            val x = size.width * targetX
                            val y = size.height * targetY
                            drawCircle(Color(0xFF5A1823), radius = 34f, center = Offset(x, y))
                            drawCircle(Red, radius = 25f, center = Offset(x, y))
                            drawCircle(Color.White, radius = 7f, center = Offset(x, y))
                        }
                        Box(Modifier.offset(
                            x = (targetX * 260).dp - 25.dp,
                            y = (targetY * 430).dp - 25.dp
                        ).size(50.dp).clickable(enabled = running) {
                            hits++
                            score += when (difficulty) { "Easy" -> 10; "Hard" -> 30; else -> 20 }
                            targetX = Random.nextFloat() * .75f + .12f
                            targetY = Random.nextFloat() * .72f + .12f
                        })
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { running = !running },
                            colors = ButtonDefaults.buttonColors(containerColor = Red),
                            modifier = Modifier.weight(1f)) { Text(if (running) "PAUSE" else "START") }
                        OutlinedButton(onClick = {
                            running = false; score = 0; hits = 0; misses = 0
                            targetX = .5f; targetY = .5f
                        }, modifier = Modifier.weight(1f)) { Text("RESET") }
                    }
                    Text("Tap the red target. Tapping elsewhere counts as a miss.", color = Muted, fontSize = 11.sp)
                }
                "Settings" -> {
                    Text("SENSITIVITY LAB", color = Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Practice sensitivity: ${sensitivity.toInt()}", color = Color.White)
                    Slider(value = sensitivity, onValueChange = { sensitivity = it }, valueRange = 1f..100f)
                    Text("Crosshair size: ${crosshairSize.toInt()} dp", color = Color.White)
                    Slider(value = crosshairSize, onValueChange = { crosshairSize = it }, valueRange = 8f..36f)
                    Text("CROSSHAIR PREVIEW", color = Muted, fontSize = 12.sp)
                    Box(Modifier.fillMaxWidth().height(180.dp).background(Panel, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center) {
                        Canvas(Modifier.size(100.dp)) {
                            val c = center
                            val r = crosshairSize
                            drawLine(Red, Offset(c.x-r,c.y), Offset(c.x+r,c.y), strokeWidth = 3f)
                            drawLine(Red, Offset(c.x,c.y-r), Offset(c.x,c.y+r), strokeWidth = 3f)
                        }
                    }
                    Text("These are practice notes and previews; they do not change Fortnite settings automatically.",
                        color = Muted, fontSize = 11.sp)
                }
                "Stats" -> {
                    Text("PERFORMANCE", color = Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    val total = hits + misses
                    val accuracy = if (total == 0) 0 else hits * 100 / total
                    StatCard("TOTAL SCORE", score.toString())
                    StatCard("TARGET HITS", hits.toString())
                    StatCard("MISSES", misses.toString())
                    StatCard("ACCURACY", "$accuracy%")
                }
            }
            Spacer(Modifier.height(2.dp))
            Text("CART3R TOOLKIT • LOCAL TRAINING • NO GAME INTEGRATION",
                color = Muted, fontSize = 9.sp, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp))
        .clickable(onClick = onClick).padding(16.dp)) {
        Text(title, color = Red, fontWeight = FontWeight.Bold)
        Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Row(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(14.dp)).padding(18.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Muted)
        Text(value, color = Red, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}
