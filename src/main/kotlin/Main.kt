import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.log10
import kotlin.math.pow
import java.util.*
import com.google.gson.reflect.TypeToken
import kotlin.system.exitProcess

private const val HEX_CHARS = "0123456789abcdef"

inline fun <reified T> String.fromJson(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)

fun String.isHex(evenLength: Boolean = false)
        : Boolean {
    if (isBlank()) {
        return false
    }
    if (evenLength && length % 2 != 0) {
        // must be even, 1-byte = 2-hex-chars
        return false
    }

    return this.all { char -> HEX_CHARS.contains(char.lowercaseChar()) }
}

fun String.hexToByteArray()
        : ByteArray {
    if (this.length % 2 != 0) {
        throw IOException("String length must be even, 1-byte = 2-hex-chars")
    }
    if (!this.isHex()) {
        throw IOException("Invalid Hex String")
    }

    val hex = lowercase(Locale.ROOT)
    val result = ByteArray(length / 2)

    for (index in hex.indices step 2) {
        val firstChar = HEX_CHARS.indexOf(hex[index])
        val secondChar = HEX_CHARS.indexOf(hex[index + 1])

        val octet = firstChar.shl(4) + secondChar
        result[index / 2] = octet.toByte()
    }

    return result
}

val MAGIC = """00010003704138EFBBBDA16A987DD901326D1C9459484C88A2861B91A312587AE70EF6237EC50E1032DC39DDE89A96A8E859D76A98A6E7E36A0CFE352CA893058234FF833FCB3B03811E9F0DC0D9A52F8045B4B2F9411B67A51C44B5EF8CE77BD6D56BA75734A1856DE6D4BED6D3A242C7C8791B3422375E5C779ABF072F7695EFA0F75BCB83789FC30E3FE4CC8392207840638949C7F688565F649B74D63D8D58FFADDA571E9554426B1318FC468983D4C8A5628B06B6FC5D507C13E7A18AC1511EB6D62EA5448F83501447A9AFB3ECC2903C9DD52F922AC9ACDBEF58C6021848D96E208732D3D1D9D9EA440D91621C7A99DB8843C59C1F2E2C7D9B577D512C166D6F7E1AAD4A774A37447E78FE2021E14A95D112A068ADA019F463C7A55685AABB6888B9246483D18B9C806F474918331782344A4B8531334B26303263D9D2EB4F4BB99602B352F6AE4046C69A5E7E8E4A18EF9BC0A2DED61310417012FD824CC116CFB7C4C1F7EC7177A17446CBDE96F3EDD88FCD052F0B888A45FDAF2B631354F40D16E5FA9C2C4EDA98E798D15E6046DC5363F3096B2C607A9D8DD55B1502A6AC7D3CC8D8C575998E7D796910C804C495235057E91ECD2637C9C1845151AC6B9A0490AE3EC6F47740A0DB0BA36D075956CEE7354EA3E9A4F2720B26550C7D394324BC0CB7E9317D8A8661F42191FF10B08256CE3FD25B745E5194906B4D61CB4C2E000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000526F6F7400000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001434130303030303030330000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000007BE8EF6CB279C9E2EEE121C6EAF44FF639F88F078B4B77ED9F9560B0358281B50E55AB721115A177703C7A30FE3AE9EF1C60BC1D974676B23A68CC04B198525BC968F11DE2DB50E4D9E7F071E562DAE2092233E9D363F61DD7C19FF3A4A91E8F6553D471DD7B84B9F1B8CE7335F0F5540563A1EAB83963E09BE901011F99546361287020E9CC0DAB487F140D6626A1836D27111F2068DE4772149151CF69C61BA60EF9D949A0F71F5499F2D39AD28C7005348293C431FFBD33F6BCA60DC7195EA2BCC56D200BAF6D06D09C41DB8DE9C720154CA4832B69C08C69CD3B073A0063602F462D338061A5EA6C915CD5623579C3EB64CE44EF586D14BAAA8834019B3EEBEED3790001000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100042EA66C66CFF335797D0497B77A197F9FE51AB5A41375DC73FD9E0B10669B1B9A5B7E8AB28F01B67B6254C14AA1331418F25BA549004C378DD72F0CE63B1F7091AAFE3809B7AC6C2876A61D60516C43A63729162D280BE21BE8E2FE057D8EB6E204242245731AB6FEE30E5335373EEBA970D531BBA2CB222D9684387D5F2A1BF75200CE0656E390CE19135B59E14F0FA5C1281A7386CCD1C8EC3FAD70FBCE74DEEE1FD05F46330B51F9B79E1DDBF4E33F14889D05282924C5F5DC2766EF0627D7EEDC736E67C2E5B93834668072216D1C78B823A072D34FF3ECF9BD11A29AF16C33BD09AFB2D74D534E027C19240D595A68EBB305ACC44AB38AB820C6D426560C000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000526F6F742D43413030303030303033000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000143503030303030303062000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000137A080BA689C590FD0B2F0D4F56B632FB934ED0739517B33A79DE040EE92DC31D37C7F73BF04BD3E44E20AB5A6FEAF5984CC1F6062E9A9FE56C3285DC6F25DDD5D0BF9FE2EFE835DF2634ED937FAB0214D104809CF74B860E6B0483F4CD2DAB2A9602BC56F0D6BD946AED6E0BE4F08F26686BD09EF7DB325F82B18F6AF2ED525BFD828B653FEE6ECE400D5A48FFE22D538BB5335B4153342D4335ACF590D0D30AE2043C7F5AD214FC9C0FE6FA40A5C86506CA6369BCEE44A32D9E695CF00B4FD79ADB568D149C2028A14C9D71B850CA365B37F70B657791FC5D728C4E18FD22557C4062D74771533C70179D3DAE8F92B117E45CB332F3B3C2A22E705CFEC66F6DA3772B000100010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010004919EBE464AD0F552CD1B72E7884910CF55A9F02E50789641D896683DC005BD0AEA87079D8AC284C675065F74C8BF37C88044409502A022980BB8AD48383F6D28A79DE39626CCB2B22A0F19E41032F094B39FF0133146DEC8F6C1A9D55CD28D9E1C47B3D11F4F5426C2C780135A2775D3CA679BC7E834F0E0FB58E68860A71330FC95791793C8FBA935A7A6908F229DEE2A0CA6B9B23B12D495A6FE19D0D72648216878605A66538DBF376899905D3445FC5C727A0E13E0E2C8971C9CFA6C60678875732A4E75523D2F562F12AABD1573BF06C94054AEFA81A71417AF9A4A066D0FFC5AD64BAB28B1FF60661F4437D49E1E0D9412EB4BCACF4CFD6A3408847982000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000526F6F742D43413030303030303033000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000158533030303030303063000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000137A0894AD505BB6C67E2E5BDD6A3BEC43D910C772E9CC290DA58588B77DCC11680BB3E29F4EABBB26E98C2601985C041BB14378E689181AAD770568E928A2B98167EE3E10D072BEEF1FA22FA2AA3E13F11E1836A92A4281EF70AAF4E462998221C6FBB9BDD017E6AC590494E9CEA9859CEB2D2A4C1766F2C33912C58F14A803E36FCCDCCCDC13FD7AE77C7A78D997E6ACC35557E0D3E9EB64B43C92F4C50D67A602DEB391B06661CD32880BD64912AF1CBCB7162A06F02565D3B0ECE4FCECDDAE8A4934DB8EE67F3017986221155D131C6C3F09AB1945C206AC70C942B36F49A1183BCD78B6E4B47C6C5CAC0F8D62F897C6953DD12F28B70C5B7DF751819A98346526250001000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000""".hexToByteArray()
val TIKTEM = """00010004d15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11ad15ea5ed15abe11a000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000526f6f742d434130303030303030332d585330303030303030630000000000000000000000000000000000000000000000000000000000000000000000000000feedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedfacefeedface010000cccccccccccccccccccccccccccccccc00000000000000000000000000aaaaaaaaaaaaaaaa00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010014000000ac000000140001001400000000000000280000000100000084000000840003000000000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000""".hexToByteArray()
const val TK = 0x140

fun makeTicket(
    titleId: String,
    titleKey: String,
    titleVersion: ByteArray,
    fullOutputPath: File,
) {
    val tikData = ByteArray(TIKTEM.size)
    System.arraycopy(TIKTEM, 0, tikData, 0, TIKTEM.size)
    System.arraycopy(titleVersion, 0, tikData, TK + 0xA6, titleVersion.size)
    System.arraycopy(titleId.hexToByteArray(), 0, tikData, TK + 0x9C, 8)
    System.arraycopy(titleKey.hexToByteArray(), 0, tikData, TK + 0x7F, 16)

    val outputFile = FileOutputStream(fullOutputPath)
    outputFile.write(tikData)
    outputFile.close()
}

fun processTitleId(
    titleId: String,
    titleKey: String,
    name: String? = null,
    region: String? = null,
    outputDir: String? = null,
    retryCount: Int = 3,
    onlineTickets: Boolean = false,
    ticketsOnly: Boolean = false
) {
    val dirname = if (name != null) {
        "$titleId - $region - $name"
    } else {
        titleId
    }

    val typecheck = titleId.substring(4, 8)
    val updatedDirname = when (typecheck) {
        "000c" -> "$dirname - DLC"
        "000e" -> "$dirname - Update"
        else -> dirname
    }

    val rawdir = File("install", safeFilename(updatedDirname))

    println("Starting work in: \"$rawdir\"")

    val targetDir = if (outputDir != null) {
        File(outputDir, rawdir.path)
    } else {
        rawdir
    }

    if (!targetDir.exists()) {
        targetDir.mkdirs()
    }

    // download TMD
    println("Downloading TMD...")

    val baseurl = "http://ccs.cdn.c.shop.nintendowifi.net/ccs/download/$titleId"
    val tmdPath = File(targetDir, "title.tmd").path

    if (!downloadFile("$baseurl/tmd", tmdPath, retryCount)) {
        println("ERROR: Could not download TMD...")
        println("MAYBE YOU ARE BLOCKING CONNECTIONS TO NINTENDO? IF YOU ARE, DON'T...! :)")
        println("Skipping title...")
        return
    }

    File(targetDir, "title.cert").writeBytes(MAGIC)

    val tmdBytes = File(tmdPath).readBytes()
    val titleVersion = tmdBytes.copyOfRange(TK + 0x9C, TK + 0x9E)

    // get ticket from keysite, from cdn if game update, or generate ticket
    if (typecheck == "000e") {
        println("\nThis is an update, so we are getting the legit ticket straight from Nintendo.")
        if (!downloadFile("$baseurl/cetk", File(targetDir, "title.tik").path, retryCount)) {
            println("ERROR: Could not download ticket from $baseurl/cetk")
            println("Skipping title...")
            return
        }
    } else if (onlineTickets) {
        val keySite = getKeySite()
        val tikUrl = "$keySite/ticket/$titleId.tik"
        if (!downloadFile(tikUrl, File(targetDir, "title.tik").path, retryCount)) {
            println("ERROR: Could not download ticket from $keySite")
            println("Skipping title...")
            return
        }
    } else {
        makeTicket(titleId, titleKey, titleVersion, File(targetDir, "title.tik"))
    }

    if (ticketsOnly) {
        println("Ticket, TMD, and CERT completed. Not downloading contents.")
        return
    }

    println("Downloading Contents...")
    val contentCount = Integer.parseInt(tmdBytes.copyOfRange(TK + 0x9E, TK + 0xA0).toHexString(), 16)

    var totalSize = 0L
    for (i in 0 until contentCount) {
        val cOffs = 0xB04 + (0x30 * i)
        totalSize += Integer.parseInt(tmdBytes.copyOfRange(cOffs + 0x08, cOffs + 0x10).toHexString(), 16)
    }
    println("Total size is ${bytesToHuman(totalSize)}\n")

    for (i in 0 until contentCount) {
        val cOffs = 0xB04 + (0x30 * i)
        val cId = tmdBytes.copyOfRange(cOffs, cOffs + 0x04).toHexString()
        val expectedSize = Integer.parseInt(tmdBytes.copyOfRange(cOffs + 0x08, cOffs + 0x10).toHexString(), 16)
        println("\nDownloading ${i + 1} of $contentCount.")
        val outFileName = File(targetDir, "$cId.app")
        val outHashFileName = File(targetDir, "$cId.h3")

        if (!downloadFile("$baseurl/$cId", outFileName.path, retryCount, expectedSize = expectedSize)) {
            println("ERROR: Could not download content file... Skipping title")
            return
        }
        if (!downloadFile("$baseurl/$cId.h3", outHashFileName.path, retryCount, ignore404 = true)) {
            println("ERROR: Could not download h3 file... Skipping title")
            return
        }
    }

    println("\nTitle download complete in \"$dirname\"\n")
}

fun safeFilename(filename: String): String {
    return filename.replace("[\\\\/:*?\"<>|]".toRegex(), "_")
}

fun ByteArray.toHexString(): String {
    return joinToString("") { "%02x".format(it) }
}

fun bytesToHuman(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = listOf("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
    return "%.1f %s".format(bytes / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}

fun downloadFile(url: String, file: String, retryCount: Int, expectedSize: Int? = null, ignore404: Boolean = false): Boolean {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()

    var retryAttempts = 0
    var successful = false

    while (!successful && retryAttempts < retryCount) {
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                if (response.code == 404 && ignore404) {
                    successful = true
                } else {
                    throw IOException("Unexpected HTTP response code: ${response.code}")
                }
            } else {
                val inputStream = response.body?.byteStream()

                if (inputStream != null) {
                    val outputFile = FileOutputStream(file)
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    var totalBytesRead: Long = 0

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputFile.write(buffer, 0, bytesRead)
                        totalBytesRead += bytesRead

                        if (expectedSize != null) {
                            val progress = ((totalBytesRead.toDouble() / expectedSize.toDouble()) * 100).toInt()
                            printProgressBar(progress)
                        }
                    }

                    outputFile.close()
                    inputStream.close()
                    successful = true
                }
            }
        } catch (e: IOException) {
            retryAttempts++
            if (retryAttempts < retryCount) {
                println("Error occurred during file download: ${e.message}")
                println("Retrying download...")
            } else {
                println("Max retry attempts reached. File download failed.")
            }
        }
    }

    return successful
}

fun printProgressBar(progress: Int) {
    print("\rDownloading: [%-50s] %d%%".format("#".repeat(progress / 2), progress))
}

fun getKeySite(): String {
    // Implement the logic to get the key site URL
    return "https://titlekeys.ovh"
}

fun getTitles(
    titles: List<String>?,
    _keys: List<String>? = null,
    onlineKeys: Boolean = false,
    onlineTickets: Boolean = false,
    outputDir: File?,
    retryCount: Int = 3,
    ticketsOnly: Boolean = false
) {
    var keys = _keys
    println("*******\nWiiUDownloader-Kotlin by Xpl0itU\n*******")

    /*if (downloadRegions.isNotEmpty() && (titles != null || _keys != null)) {
        println("If using '-region', don't give Title IDs or keys, it gets all titles from the keysite")
        return
    }*/
    if (keys != null && keys.size != titles?.size) {
        println("Number of keys and Title IDs do not match up")
        return
    }
    if (titles != null && keys == null && !onlineKeys && !onlineTickets) {
        println("You also need to provide '-keys' or use '-onlinekeys' or '-onlinetickets'")
        return
    }

    val keysite = getKeySite()
    println("Downloading/updating data from $keysite")
    try {
        if (!downloadFile("$keysite/json", "titlekeys.json", retryCount)) {
            println("\nERROR: Could not download data file...")
        }
    } catch (e: Exception) {
        println("\nThe saved keysite doesn't appear to be a valid URL.")
    }

    println("Downloaded data OK!")

    val titleKeysData: List<Map<String, Any>> = File("titlekeys.json").readText().fromJson<List<Map<String, Any>>>()

    titles?.forEach { titleId ->
        val lowerTitleId = titleId.uppercase(Locale.getDefault())
        if (lowerTitleId.length != 16) {
            println("The Title ID(s) must be 16 hexadecimal characters long")
            println("$titleId - is not ok.")
            return
        }
        var titleKey: String? = null
        var name: String? = null
        var region: String? = null

        val patch = lowerTitleId.substring(4, 8) == "000e"

        if (keys != null) {
            titleKey = keys!![keys!!.size - 1]
            keys = keys!!.subList(0, keys!!.size - 1)
            if (titleKey.length != 32) {
                println("The key(s) must be 32 hexadecimal characters long")
                println("$titleId - is not ok.")
                return
            }
        } else {
            val titleData = titleKeysData.firstOrNull { it["titleID"] == lowerTitleId }

            if (!patch) {
                if (titleData == null) {
                    println("ERROR: Could not find data on $keysite for $titleId, skipping")
                    return@forEach
                } else if (onlineTickets && titleData["ticket"] == "0") {
                    println("ERROR: Ticket not available on $keysite for $titleId")
                    return@forEach
                } else if (onlineKeys) {
                    titleKey = titleData["titleKey"] as? String
                }
            }

            if (titleData != null) {
                name = titleData["name"] as? String
                region = titleData["region"] as? String
            }
        }

        if (!(titleKey != null || onlineTickets || patch)) {
            println("ERROR: Could not find title or ticket for $titleId")
            return@forEach
        }

        if (titleKey != null) {
            if (outputDir != null) {
                processTitleId(
                    titleId = lowerTitleId,
                    titleKey = titleKey,
                    name = name,
                    region = region,
                    outputDir = outputDir.path,
                    retryCount = retryCount,
                    onlineTickets = onlineTickets,
                    ticketsOnly = ticketsOnly
                )
            }
        }
    }
}

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: [TitleID]")
        exitProcess(1)
    }
    if (args[0].length != 16) {
        println("Wrong title id length, must be 16")
        exitProcess(1)
    }
    getTitles(listOf(args[0]), onlineKeys = true, outputDir = File("install"))
}