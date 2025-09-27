# Escolher o sistema: "mono", "modular" ou "micro"
$systemType = "modular"

$env:BASE_URL = "http://localhost:8080"

Write-Output "Running $systemType performance tests..."

Write-Output "First Tests : aprox 10 min"
k6 run login_demo_volunteer.js --summary-export ".\$systemType\${systemType}_login_vol_1.json"
k6 run login_demo_volunteer.js --summary-export ".\$systemType\${systemType}_login_vol_2.json"
k6 run login_demo_volunteer.js --summary-export ".\$systemType\${systemType}_login_vol_3.json"

Write-Output "Second Tests : aprox 10 min"
k6 run admin_login_get_themes.js --summary-export ".\$systemType\${systemType}_admin_themes_1.json"
k6 run admin_login_get_themes.js --summary-export ".\$systemType\${systemType}_admin_themes_2.json"
k6 run admin_login_get_themes.js --summary-export ".\$systemType\${systemType}_admin_themes_3.json"

Write-Output "Third Tests : aprox 10 min"
k6 run register_volunteer_multipart.js --summary-export ".\$systemType\${systemType}_register_vol_1.json"
k6 run register_volunteer_multipart.js --summary-export ".\$systemType\${systemType}_register_vol_2.json"
k6 run register_volunteer_multipart.js --summary-export ".\$systemType\${systemType}_register_vol_3.json"
