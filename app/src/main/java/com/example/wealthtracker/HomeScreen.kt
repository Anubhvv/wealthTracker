package com.example.wealthtracker
import android.content.Context
import android.graphics.Rect
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clipToBounds

@Composable
fun HomeScreen(context: Context, primaryColor: Color = MaterialTheme.colorScheme.primary) {
    var wealthData by remember { mutableStateOf(loadWealthData(context = context)) }

    var currency by remember { mutableStateOf("USD") }
    // Create a list of categories
    val categories by remember { mutableStateOf(wealthData.categories) }

    // Calculate the total wealth
    var totalWealth by remember { mutableStateOf(wealthData.categories.sumByDouble { it.value }) }

    // Define the number of levels and initial target
    val totalLevels = 24
    val initialTarget = 25000.0 // Adjust as needed for your progression system

    // Calculate the current level based on progress
    val currentLevel by remember {
        mutableStateOf(
            calculateCurrentLevel(
                totalWealth,
                totalLevels,
                initialTarget
            )
        )
    }
    // Calculate the progress as a percentage
    var progress by remember { mutableStateOf((totalWealth / calculateTargetForLevel(currentLevel, initialTarget).toInt()) * 100) } // Assuming the target is 1 million


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = primaryColor,
            text = "Wealth Monitor",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(1.dp)
                .wrapContentHeight()
        )

        Text(
            text = "Level: $currentLevel / $totalLevels",
            fontSize = 20.sp,
            modifier = Modifier.padding(8.dp),
            fontWeight = FontWeight.Bold,
        )

        Text(
            color = Color.Gray,
            text = "Target for this level: ${calculateTargetForLevel(currentLevel, initialTarget).toInt()} $currency",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Circular Progress Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .height(200.dp)
                .width(200.dp)
                .padding(8.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Canvas(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clipToBounds()
                    .padding(vertical = 36.dp, horizontal = 36.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val center = Offset(canvasWidth / 2, canvasHeight / 2)
                val radius = (canvasWidth / 2) - 16.dp.toPx()
                val strokeWidth = 24.dp.toPx()

                drawArc(
                    color = Color.Gray,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = true,
                    style = Stroke(strokeWidth),
                    size = Size(canvasWidth, canvasHeight),
                )

                // Draw progress arc
                val sweepAngle = (progress * 3.6).toFloat() // 3.6 degrees per percentage

                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(strokeWidth),
                    size = Size(canvasWidth, canvasHeight),
                )


                // Draw current net worth text
                var netWorthY = center.y + (strokeWidth / 2)
                val percentageText = "    ${progress.toInt()}%"
                val netWorthText = "       ${totalWealth.toInt()} $"
                val textWidth = center.x - (strokeWidth / 2)
                val textHeight = center.y + (strokeWidth / 2)
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint()
                    val textSize = 33.sp.toPx()
                    paint.textSize = textSize
                    paint.color = primaryColor.toArgb()
                    paint.textAlign = android.graphics.Paint.Align.CENTER
                    val percentageTextBounds = Rect()
                    paint.getTextBounds(percentageText, 0, percentageText.length, percentageTextBounds)

                    val percentageTextY = textHeight - (percentageTextBounds.height() / 2f)
                    netWorthY = percentageTextY + percentageTextBounds.height() + 10.dp.toPx() // Adjust the vertical spacing between percentageText and netWorthText as needed


                    canvas.nativeCanvas.drawText(
                        percentageText,
                        textWidth,
                        percentageTextY,
                        paint
                    )
                }

                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint()
                    val textSize = 18.sp.toPx()
                    paint.textSize = textSize
                    paint.color = primaryColor.toArgb()
                    paint.textAlign = android.graphics.Paint.Align.CENTER
                    canvas.nativeCanvas.drawText(
                        netWorthText,
                        textWidth,
                        netWorthY,
                        paint
                    )
                }

            }
            Spacer(modifier = Modifier.height(150.dp))
        }
        Spacer(modifier = Modifier.height(70.dp))


    }
}
