# Script to convert WebP images to PNG
$extrasPath = ".\src\restaurantSystem\extras"
$webpFiles = Get-ChildItem -Path $extrasPath -Filter "*.webp"

foreach ($file in $webpFiles) {
    $inputPath = $file.FullName
    $outputPath = [System.IO.Path]::Combine($extrasPath, $file.BaseName + ".png")
    
    Write-Host "Converting: $($file.Name) -> $($file.BaseName).png"
    
    # Use .NET to load and save the image
    Add-Type -AssemblyName System.Drawing
    $image = [System.Drawing.Image]::FromFile($inputPath)
    $image.Save($outputPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $image.Dispose()
    
    Write-Host "  Done!"
}

Write-Host "All WebP files converted to PNG!"
