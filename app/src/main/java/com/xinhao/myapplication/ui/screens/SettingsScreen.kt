package com.xinhao.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.sharp.*
import androidx.compose.material.icons.twotone.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import com.xinhao.myapplication.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    isDarkMode: Boolean = false,
    onDarkModeChanged: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showExportDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showFeedbackDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "settings_animation")
    val backgroundOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "background_offset"
    )
    
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // 动态背景渐变
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.02f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.01f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.005f)
                        ),
                        start = androidx.compose.ui.geometry.Offset(backgroundOffset, 0f),
                        end = androidx.compose.ui.geometry.Offset(1f - backgroundOffset, 1f)
                    )
                )
        )
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            "设置",
                            fontWeight = FontWeight.Bold
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 个人设置
                item {
                    SettingsSection(title = "个人设置")
                }
                
                item {
                    ProfileCard(
                        onEditClick = { showEditProfileDialog = true }
                    )
                }
                
                // 应用设置
                item {
                    SettingsSection(title = "应用设置")
                }
                
                item {
                    SettingItem(
                        icon = Icons.Filled.DarkMode,
                        title = "深色模式",
                        subtitle = "切换应用主题",
                        trailing = {
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { onDarkModeChanged(it) }
                            )
                        }
                    )
                }
                
                // 数据管理
                item {
                    SettingsSection(title = "数据管理")
                }
                
                item {
                    SettingItem(
                        icon = Icons.Filled.Download,
                        title = "导出数据",
                        subtitle = "导出交易记录为CSV",
                        onClick = { showExportDialog = true }
                    )
                }
                
                item {
                    SettingItem(
                        icon = Icons.Filled.Upload,
                        title = "导入数据",
                        subtitle = "从文件导入数据",
                        onClick = { /* TODO: 实现导入功能 */ }
                    )
                }
                
                item {
                    SettingItem(
                        icon = Icons.Filled.DeleteForever,
                        title = "清除数据",
                        subtitle = "删除所有本地数据",
                        onClick = { showClearDataDialog = true },
                        textColor = Color(0xFFF44336)
                    )
                }
                
                // 关于应用
                item {
                    SettingsSection(title = "关于应用")
                }
                
                item {
                    SettingItem(
                        icon = Icons.Filled.Info,
                        title = "应用信息",
                        subtitle = "版本 1.0.0",
                        onClick = { showAboutDialog = true }
                    )
                }
                
                item {
                    SettingItem(
                        icon = Icons.Filled.Feedback,
                        title = "意见反馈",
                        subtitle = "微信: DXH08060927  ·  QQ: 3878919117  ·  GitHub",
                        onClick = { showFeedbackDialog = true }
                    )
                }
            }
        }
    }
    
    // 导出数据对话框
    if (showExportDialog) {
        ExportDataDialog(
            onConfirm = { 
                // TODO: 实现导出功能
                showExportDialog = false 
            },
            onDismiss = { showExportDialog = false }
        )
    }
    
    // 清除数据确认对话框
    if (showClearDataDialog) {
        ClearDataDialog(
            onConfirm = { 
                // TODO: 实现清除数据功能
                showClearDataDialog = false 
            },
            onDismiss = { showClearDataDialog = false }
        )
    }
    
    // 关于应用对话框
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
    
    if (showFeedbackDialog) {
        FeedbackDialog(
            onDismiss = { showFeedbackDialog = false }
        )
    }
    
    // 编辑个人信息对话框
    if (showEditProfileDialog) {
        EditProfileDialog(
            onDismiss = { showEditProfileDialog = false }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(horizontal = 4.dp, vertical = 8.dp)
    )
}

@Composable
fun ProfileCard(
    onEditClick: () -> Unit
) {
    CardContainer {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(32.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "用户",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "管理您的个人设置",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(onClick = onEditClick) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "编辑",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    CardContainer(
        modifier = modifier.clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(
                        elevation = 1.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
                
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (trailing != null) {
                trailing()
            } else if (onClick != null) {
                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ExportDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedFormat by remember { mutableStateOf("CSV") }
    var includeImages by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "导出数据",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                Text(
                    text = "选择导出格式",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("CSV", "JSON", "Excel").forEach { format ->
                        FilterChip(
                            selected = selectedFormat == format,
                            onClick = { selectedFormat = format },
                            label = { Text(format) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = includeImages,
                        onCheckedChange = { includeImages = it }
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text("包含图片附件")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("导出")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ClearDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "清除数据",
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF44336)
            ) 
        },
        text = {
            Text(
                "此操作将删除所有本地数据，包括交易记录、分类设置等。此操作无法撤销，请谨慎操作。",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFF44336)
                )
            ) {
                Text("确认删除")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "关于应用",
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column {
                Text(
                    text = "个人财务管理",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("版本: 1.0.0")
                Text("开发者: XinHao")
                Text("描述: 一款简单易用的个人财务管理应用，帮助您记录收支、分析消费、制定预算。")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "功能特点:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                
                Text("• 简单记账")
                Text("• 分类管理")
                Text("• 数据统计")
                Text("• 预算控制")
                Text("• 数据导出")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("确定")
            }
        }
    )
}

@Composable
fun FeedbackDialog(
    onDismiss: () -> Unit
) {
    val wechat = "DXH08060927"
    val qq = "3878919117"
    val githubUrl = "https://github.com/MSDOS-Signal"

    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val context = androidx.compose.ui.platform.LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "意见反馈",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("您可以通过以下方式联系作者：")

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("微信: $wechat", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(wechat))
                        android.widget.Toast.makeText(context, "已复制微信号", android.widget.Toast.LENGTH_SHORT).show()
                    }) { Text("复制") }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("QQ: $qq", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(qq))
                        android.widget.Toast.makeText(context, "已复制QQ号", android.widget.Toast.LENGTH_SHORT).show()
                    }) { Text("复制") }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("GitHub: $githubUrl", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(githubUrl))
                        context.startActivity(intent)
                    }) { Text("打开") }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("确定") }
        }
    )
}

@Composable
fun EditProfileDialog(
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf("用户") }
    var showImagePicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "编辑个人信息",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 头像选择
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .shadow(
                                elevation = 1.dp,
                                shape = RoundedCornerShape(32.dp),
                                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                    )
                                ),
                                shape = RoundedCornerShape(32.dp)
                            )
                            .clickable { showImagePicker = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "点击更换头像",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "支持JPG、PNG格式",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // 用户名输入
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("用户名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: 保存用户信息
                    onDismiss()
                }
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

