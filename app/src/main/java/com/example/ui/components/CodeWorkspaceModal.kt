package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BorderDark
import com.example.ui.theme.BorderLight
import com.example.ui.theme.CodeBoxBackground
import com.example.ui.theme.ElevatedDark
import com.example.ui.theme.GrayMuted
import com.example.ui.theme.GraySubtle
import com.example.ui.theme.OffWhite
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite

@Composable
fun CodeWorkspaceModal(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(0) }
    var codeSnippet by remember {
        mutableStateOf(
            """
            // ALPHA Neural Runtime - High Throughput Kernel
            package com.aether.alpha.runtime

            class TensorKernelMatrix(
                val dimensions: Int = 512
            ) {
                private val weights = FloatArray(dimensions * dimensions)

                fun forwardPropagate(vector: FloatArray): FloatArray {
                    require(vector.size == dimensions) { "Dimension mismatch" }
                    val result = FloatArray(dimensions)
                    for (i in 0 until dimensions) {
                        var sum = 0f
                        val rowOffset = i * dimensions
                        for (j in 0 until dimensions) {
                            sum += weights[rowOffset + j] * vector[j]
                        }
                        result[i] = kotlin.math.max(0f, sum)
                    }
                    return result
                }
            }
            """.trimIndent()
        )
    }

    var consoleOutput by remember {
        mutableStateOf(
            """
            [AETHER RUNTIME] Initializing ALPHA Tensor Virtual Machine...
            [INFO] Allocated 512x512 matrix memory block: 1024 KB.
            [INFO] Zero heap fragmentations detected.
            [EXECUTION] forwardPropagate() completed in 1.2ms.
            [VERIFICATION] Kernel status: VERIFIED & OPTIMAL.
            """.trimIndent()
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_close_code_workspace")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Close",
                    tint = PureWhite
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "AETHER CODE WORKSPACE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GrayMuted
                )
                Text(
                    text = "ALPHA Neural Playground",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {
                    consoleOutput = """
                        [AETHER RUNTIME] Recompiling Tensor Kernel...
                        [INFO] Dimensions: 512
                        [BENCHMARK] Latency: 0.98ms (Hardware accelerated)
                        [STATUS] Ready for deployment into ALPHA mesh.
                    """.trimIndent()
                    Toast.makeText(context, "Kernel executed", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PureWhite)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Execute",
                    tint = PureBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Tabs: Editor / Console
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = ElevatedDark,
            contentColor = PureWhite,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PureWhite
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("SOURCE CODE (.kt)", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = PureWhite) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("CONSOLE TELEMETRY", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = PureWhite) }
            )
        }

        if (selectedTab == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CodeBoxBackground)
                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
            ) {
                OutlinedTextField(
                    value = codeSnippet,
                    onValueChange = { codeSnippet = it },
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = OffWhite
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = OffWhite,
                        unfocusedTextColor = OffWhite,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CodeBoxBackground)
                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TERMINAL // AETHER RUNTIME v1.4",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = GrayMuted
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(consoleOutput))
                            Toast.makeText(context, "Telemetry copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy logs",
                            tint = GraySubtle,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Divider(color = BorderDark, modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = consoleOutput,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.5.sp,
                    lineHeight = 18.sp,
                    color = OffWhite
                )
            }
        }
    }
}
