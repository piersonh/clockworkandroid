package com.wordco.clockworkandroid.user_stats_feature.ui.composables

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wordco.clockworkandroid.core.ui.theme.ClockworkTheme
import com.wordco.clockworkandroid.user_stats_feature.ui.model.LineChartDataPoint
import kotlin.math.roundToInt

private data class ChartBounds(
    val minX: Float,
    val maxX: Float,
    val minY: Float,
    val maxY: Float,
    val xRange: Float,
    val yRange: Float
)


@Composable
fun LineChart(
    dataPoints: List<LineChartDataPoint>,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    // Chart line/point properties
    minX: Float? = null,
    maxX: Float? = null,
    minY: Float? = null,
    maxY: Float? = null,
    lineStrokeWidth: Dp = 3.dp,
    pointColor: Color = MaterialTheme.colorScheme.primary,
    pointRadius: Dp = 6.dp,
    lineColor: Color = MaterialTheme.colorScheme.primary,
    fillBrush: Brush? = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            Color.Transparent
        )
    ),
    // Grid and Label properties
    xAxisIntervals: Int = 4,
    yAxisIntervals: Int = 4,
    yAxisLabelWidth: Dp = 40.dp,
    yAxisLabelPadding: Dp = 8.dp,
    axisHeaderPadding: Dp = 16.dp,
    formatXLabel: (Float) -> String = { it.roundToInt().toString() },
    formatYLabel: (Float) -> String = { it.roundToInt().toString() },
    gridLineColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
    gridStrokeWidth: Dp = 1.dp,
    axisLabelColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    axisLabelStyle: TextStyle = MaterialTheme.typography.labelSmall
) {
    // Return early if there's no data to draw
    if (dataPoints.isEmpty()) return

    // --- 1. Animation ---
    // Animate Y-values from 0f to 1f (representing 0% to 100% of their value)
    val animationProgress = remember(dataPoints) { Animatable(1f /*ignore this*/) }
    LaunchedEffect(dataPoints) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // --- 2. Data Bounds (Cached) ---
    val bounds = remember(dataPoints, minX, maxX, minY, maxY) {
        // Find min/max from data first
        val autoMinX = dataPoints.minOf { it.x }
        val autoMaxX = dataPoints.maxOf { it.x }
        val autoMinY = dataPoints.minOf { it.y }
        val autoMaxY = dataPoints.maxOf { it.y }

        // Apply user overrides if they exist
        val finalMinX = minX ?: autoMinX
        val finalMaxX = maxX ?: autoMaxX
        val finalMinY = minY ?: autoMinY
        val finalMaxY = maxY ?: autoMaxY

        ChartBounds(
            minX = finalMinX,
            maxX = finalMaxX,
            minY = finalMinY,
            maxY = finalMaxY,
            xRange = if (finalMaxX - finalMinX == 0f) 1f else finalMaxX - finalMinX,
            yRange = if (finalMaxY - finalMinY == 0f) 1f else finalMaxY - finalMinY
        )
    }

    // --- 3. Generate Label Values (Cached) ---
    val xLabelValues = remember(bounds, xAxisIntervals) {
        (0..xAxisIntervals).map { i ->
            bounds.minX + (i * (bounds.xRange / xAxisIntervals))
        }
    }
    val yLabelValues = remember(bounds, yAxisIntervals) {
        (0..yAxisIntervals).map { i ->
            bounds.minY + (i * (bounds.yRange / yAxisIntervals))
        }
    }

    // --- 4. Accessibility ---
    val accessibilityModifier = contentDescription?.let {
        Modifier.semantics {
            this.contentDescription = it
        }
    } ?: Modifier

    Column(modifier = modifier.then(accessibilityModifier)) {
        // --- 5. Chart Area + Y-Axis Labels ---
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // --- Y-Axis Labels ---
            Layout(
                content = {
                    // Y-labels are drawn top-to-bottom, so we reverse the list
                    yLabelValues.asReversed().forEach { labelValue ->
                        Text(
                            text = formatYLabel(labelValue),
                            color = axisLabelColor,
                            style = axisLabelStyle,
                            textAlign = TextAlign.End,
                        )
                    }
                },
                modifier = Modifier
                    //.fillMaxHeight()
                    .width(yAxisLabelWidth)
                    .padding(vertical = axisHeaderPadding) // Padding for top/bottom alignment
                    .padding(end = yAxisLabelPadding)
            ) { measurables, constraints ->

                // --- 1. Measure all the labels ---
                // Pass unmodified constraints to respect width
                val placeables = measurables.map { it.measure(constraints) }
                val totalHeight = constraints.maxHeight
                val numIntervals = yLabelValues.size - 1

                // Layout width is just the constraint's max width
                val width = constraints.maxWidth

                // --- 2. Place labels at correct positions ---
                layout(width, totalHeight) {
                    if (numIntervals <= 0) return@layout

                    placeables.forEachIndexed { index, placeable ->
                        // Calculate the y position of the grid line
                        val yPosition = (index.toFloat() / numIntervals) * totalHeight

                        // Adjust position to be vertically centered on the grid line
                        val yOffset = (yPosition - (placeable.height / 2)).toInt()

                        // Place at the calculated offset, x=0
                        placeable.placeRelative(x = 0, y = yOffset)
                    }
                }
            }

            // --- Chart Canvas ---
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = axisHeaderPadding, vertical = axisHeaderPadding)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val progress = animationProgress.value

                val lineStrokeWidthPx = lineStrokeWidth.toPx()
                val pointRadiusPx = pointRadius.toPx()
                val gridStrokeWidthPx = gridStrokeWidth.toPx()

                // --- Coordinate Transformation ---
                // This now animates the Y-position based on progress
                fun transform(point: LineChartDataPoint): Offset {
                    // Animate the Y value from minY to its actual value
                    val animatedY = bounds.minY + (point.y - bounds.minY) * progress

                    // Scale X to the canvas width
                    val xPx = (point.x - bounds.minX) / bounds.xRange * canvasWidth

                    // Scale Y to the canvas height
                    val yPx = canvasHeight - ((animatedY - bounds.minY) / bounds.yRange * canvasHeight)

                    return Offset(xPx, yPx)
                }

                // --- Draw Grid Lines ---
                // Vertical grid lines
                xLabelValues.forEach { xValue ->
                    val xPx = transform(LineChartDataPoint(xValue, bounds.minY)).x
                    drawLine(
                        color = gridLineColor,
                        strokeWidth = gridStrokeWidthPx,
                        start = Offset(xPx, 0f),
                        end = Offset(xPx, canvasHeight)
                    )
                }
                // Horizontal grid lines
                yLabelValues.forEach { yValue ->
                    // Use progress = 1f for grid lines so they are static
                    val yPx = canvasHeight - ((yValue - bounds.minY) / bounds.yRange * canvasHeight)
                    drawLine(
                        color = gridLineColor,
                        strokeWidth = gridStrokeWidthPx,
                        start = Offset(0f, yPx),
                        end = Offset(canvasWidth, yPx)
                    )
                }

                // --- Create Paths (Line and Fill) ---
                val linePath = Path()
                val fillPath = Path()

                val firstPoint = transform(dataPoints.first())
                linePath.moveTo(firstPoint.x, firstPoint.y)
                fillPath.moveTo(firstPoint.x, canvasHeight) // Start fill at bottom
                fillPath.lineTo(firstPoint.x, firstPoint.y) // Line to first data point

                // Iterate through the rest of the points
                dataPoints.drop(1).forEach { point ->
                    val nextPoint = transform(point)
                    linePath.lineTo(nextPoint.x, nextPoint.y)
                    fillPath.lineTo(nextPoint.x, nextPoint.y)
                }

                // Close the fill path at the bottom edge
                val lastPoint = transform(dataPoints.last())
                fillPath.lineTo(lastPoint.x, canvasHeight)
                fillPath.close()

                // --- Draw Fill (behind the line) ---
                if (fillBrush != null) {
                    drawPath(
                        path = fillPath,
                        brush = fillBrush
                    )
                }

                // --- Draw the Line ---
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = lineStrokeWidthPx)
                )

                // --- Draw Points ---
                dataPoints.forEach { point ->
                    drawCircle(
                        color = pointColor,
                        radius = pointRadiusPx,
                        center = transform(point)
                    )
                }
            }
        }

        // --- 6. X-Axis Labels ---
        Layout(
            content = {
                xLabelValues.forEach { labelValue ->
                    Text(
                        text = formatXLabel(labelValue),
                        color = axisLabelColor,
                        style = axisLabelStyle,
                    )
                }
            },
            modifier = Modifier
                .padding(
                    start = yAxisLabelWidth + axisHeaderPadding,
                    end = axisHeaderPadding
                )
        ) { measurables, constraints ->

            // --- 1. Measure all the labels ---
            val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
            val totalWidth = constraints.maxWidth
            val numIntervals = xLabelValues.size - 1
            val height = placeables.maxOfOrNull { it.height } ?: 0

            // --- 2. Place labels at correct positions ---
            layout(totalWidth, height) {
                if (numIntervals <= 0) return@layout // Edge case

                placeables.forEachIndexed { index, placeable ->

                    // Get the data value for this label
                    val xValue = xLabelValues[index]

                    // Calculate the x position based on its *value*, not its index
                    val xPosition = ((xValue - bounds.minX) / bounds.xRange) * totalWidth

                    // This logic is now correct, as it uses the value-based xPosition
                    val xOffset = when (index) {
                        0 -> 0 // First label, align start
                        placeables.lastIndex -> (xPosition - placeable.width).toInt() // Last, align end
                        else -> (xPosition - (placeable.width / 2)).toInt() // Middle, align center
                    }

                    placeable.placeRelative(x = xOffset, y = 0)
                }
            }
        }
    }
}

@Preview
@Composable
private fun LineChartPreview() {
    ClockworkTheme {
        val data = listOf(
            LineChartDataPoint(0f, 10f),
            LineChartDataPoint(1.2f, 50f),
            LineChartDataPoint(2.3f, 20f),
            LineChartDataPoint(3f, 45f),
            LineChartDataPoint(4.7f, 15f),
        )

        Surface(
            modifier = Modifier.padding(16.dp)
        ) {
            LineChart(
                dataPoints = data,
                modifier = Modifier.fillMaxSize(),
                pointColor = MaterialTheme.colorScheme.secondary,
                lineColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                minY = 0f,
                yAxisIntervals = 5,
            )
        }
    }
}