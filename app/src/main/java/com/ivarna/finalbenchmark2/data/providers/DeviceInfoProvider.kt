package com.ivarna.finalbenchmark2.data.providers

import android.content.Context
import com.ivarna.finalbenchmark2.R
import com.ivarna.finalbenchmark2.domain.model.ItemValue
import com.ivarna.finalbenchmark2.utils.CpuNativeBridge
import com.ivarna.finalbenchmark2.utils.DeviceInfoCollector
import com.ivarna.finalbenchmark2.utils.GpuInfoUtils

class DeviceInfoProvider {
    
    suspend fun getData(context: Context): List<ItemValue> {
        val deviceInfo = DeviceInfoCollector.getDeviceInfo(context)
        val gpuInfoUtils = GpuInfoUtils(context)
        val gpuInfoState = gpuInfoUtils.getGpuInfo()
        
        return buildList {
            // Device section
            add(ItemValue.Text(context.getString(R.string.device), ""))
            add(ItemValue.Text(context.getString(R.string.model), "${deviceInfo.manufacturer} ${deviceInfo.deviceModel}"))
            add(ItemValue.Text(context.getString(R.string.board), deviceInfo.board))
            add(ItemValue.Text(context.getString(R.string.soc), deviceInfo.socName))
            add(ItemValue.Text(context.getString(R.string.architecture), deviceInfo.cpuArchitecture))
            
            // CPU section
            add(ItemValue.Text(context.getString(R.string.cpu), ""))
            
            // Get detailed CPU information from native bridge
            val cpuNative = CpuNativeBridge()
            val details = cpuNative.getCpuDetails()
            
            // Add processor details
            add(ItemValue.Text(context.getString(R.string.soc_name), details.socName))
            add(ItemValue.Text(context.getString(R.string.abi), details.abi))
            add(ItemValue.Text(context.getString(R.string.arm_neon), if(details.hasNeon) "Yes" else "No"))
            
            // Add cache configuration
            if (details.caches.isNotEmpty()) {
                add(ItemValue.Text(context.getString(R.string.cache_configuration), ""))
                details.caches.forEach { cache ->
                    // Format: "L1 Instruction" -> "64KB"
                    val name = "L${cache.level} ${cache.type.replaceFirstChar { it.uppercase() }}"
                    add(ItemValue.Text(name, cache.size))
                }
            }
            
            // Add basic core information
            add(ItemValue.Text(context.getString(R.string.total_cores), deviceInfo.totalCores.toString()))
            add(ItemValue.Text(context.getString(R.string.big_cores), deviceInfo.bigCores.toString()))
            add(ItemValue.Text(context.getString(R.string.small_cores), deviceInfo.smallCores.toString()))
            add(ItemValue.Text(context.getString(R.string.cluster_topology), deviceInfo.clusterTopology))
            
            // Add CPU frequencies
            deviceInfo.cpuFrequencies.forEach { (core, freq) ->
                add(ItemValue.Text("Core ${core} Frequency", freq))
            }
            
            // GPU section
            add(ItemValue.Text(context.getString(R.string.gpu), ""))
            add(ItemValue.Text(context.getString(R.string.model), deviceInfo.gpuModel))
            add(ItemValue.Text(context.getString(R.string.vendor), deviceInfo.gpuVendor))
            
            // Add detailed GPU information if available
            if (gpuInfoState is com.ivarna.finalbenchmark2.utils.GpuInfoState.Success) {
                val gpuInfo = gpuInfoState.gpuInfo
                add(ItemValue.Text(context.getString(R.string.opengl_es), gpuInfo.basicInfo.openGLVersion))
                
                // Vulkan information
                gpuInfo.vulkanInfo?.let { vulkanInfo ->
                    add(ItemValue.Text(context.getString(R.string.vulkan_support), if (vulkanInfo.supported) "Yes" else "No"))
                    if (vulkanInfo.supported) {
                        vulkanInfo.apiVersion?.let { add(ItemValue.Text(context.getString(R.string.vulkan_api_version), it)) }
                        vulkanInfo.driverVersion?.let { add(ItemValue.Text(context.getString(R.string.vulkan_driver_version), it)) }
                        vulkanInfo.physicalDeviceName?.let { add(ItemValue.Text(context.getString(R.string.physical_device), it)) }
                        vulkanInfo.physicalDeviceType?.let { add(ItemValue.Text(context.getString(R.string.device_type), it)) }
                        
                        // Add extension counts
                        add(ItemValue.Text(context.getString(R.string.vulkan_instance_extensions), "${vulkanInfo.instanceExtensions.size}"))
                        add(ItemValue.Text(context.getString(R.string.vulkan_device_extensions), "${vulkanInfo.deviceExtensions.size}"))
                        
                        // Add some key features
                        vulkanInfo.features?.let { features ->
                            add(ItemValue.Text(context.getString(R.string.geometry_shader), if (features.geometryShader) "Yes" else "No"))
                            add(ItemValue.Text(context.getString(R.string.tessellation_shader), if (features.tessellationShader) "Yes" else "No"))
                        }
                        
                        // Add memory heap information
                        vulkanInfo.memoryHeaps?.let { memoryHeaps ->
                            add(ItemValue.Text(context.getString(R.string.vulkan_memory_heaps), "${memoryHeaps.size}"))
                            // Add total memory from first heap as an example
                            if (memoryHeaps.isNotEmpty()) {
                                val largestHeap = memoryHeaps.maxByOrNull { it.size }
                                largestHeap?.let {
                                    add(ItemValue.Text(context.getString(R.string.largest_memory_heap), formatBytes(it.size)))
                                }
                            }
                        }
                    }
                }
            }
            
            // Memory section
            add(ItemValue.Text(context.getString(R.string.memory), ""))
            add(ItemValue.Text(context.getString(R.string.total_ram), formatBytes(deviceInfo.totalRam)))
            add(ItemValue.Text(context.getString(R.string.available_ram), formatBytes(deviceInfo.availableRam)))
            
            // Storage section
            add(ItemValue.Text(context.getString(R.string.storage), ""))
            add(ItemValue.Text(context.getString(R.string.total_storage), formatBytes(deviceInfo.totalStorage)))
            add(ItemValue.Text(context.getString(R.string.free_storage), formatBytes(deviceInfo.freeStorage)))
            
            // System section
            add(ItemValue.Text(context.getString(R.string.system), ""))
            add(ItemValue.Text(context.getString(R.string.android_version), "${deviceInfo.androidVersion} (API ${deviceInfo.apiLevel})"))
            add(ItemValue.Text(context.getString(R.string.kernel_version), deviceInfo.kernelVersion))
            
            // Battery section (if available)
            if (deviceInfo.batteryTemperature != null) {
                add(ItemValue.Text(context.getString(R.string.battery), ""))
                add(ItemValue.Text(context.getString(R.string.temperature), "${deviceInfo.batteryTemperature}°C"))
                if (deviceInfo.batteryCapacity != null) {
                    add(ItemValue.Text(context.getString(R.string.capacity), "${deviceInfo.batteryCapacity}%"))
                }
            }
        }
    }
    
    private fun formatBytes(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) return "$bytes B"
        val exp = (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "B"
        return String.format("%.1f %s", bytes / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }
}