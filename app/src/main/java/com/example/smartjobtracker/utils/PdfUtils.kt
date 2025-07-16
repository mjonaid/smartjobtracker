package com.example.smartjobtracker.utils

import android.content.Context
import android.content.Intent
import android.graphics.pdf.PdfDocument
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.smartjobtracker.data.JobEntity
import java.io.File
import java.io.FileOutputStream

// ✅ Generate PDF and save in external app-specific directory
fun generateJobListPdf(context: Context, jobs: List<JobEntity>): File {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    var yPosition = 50
    val paint = android.graphics.Paint().apply {
        textSize = 14f
        isAntiAlias = true
    }

    canvas.drawText("Job Tracker Report", 200f, yPosition.toFloat(), paint)
    yPosition += 30

    jobs.forEach { job ->
        canvas.drawText("Title: ${job.title}", 50f, yPosition.toFloat(), paint)
        yPosition += 20
        canvas.drawText("Company: ${job.company}", 50f, yPosition.toFloat(), paint)
        yPosition += 20
        canvas.drawText("Status: ${job.status}", 50f, yPosition.toFloat(), paint)
        yPosition += 30
    }

    pdfDocument.finishPage(page)

    // ✅ Save PDF in app-specific external Documents directory
    val file = File(
        context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
        "job_list.pdf"
    )
    pdfDocument.writeTo(FileOutputStream(file))
    pdfDocument.close()

    return file
}

// ✅ Share PDF safely using FileProvider
fun sharePdf(context: Context, pdfFile: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider", // must match Manifest
        pdfFile
    )

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Job Tracker Report")
        putExtra(Intent.EXTRA_TEXT, "Please find attached the job tracker report.")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(intent, "Share PDF via"))
}
