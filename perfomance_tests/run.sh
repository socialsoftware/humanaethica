

export BASE_URL="http://localhost:8080"



echo "Running modular performance tests..."


echo "First Tests : aprox 10 min "
# Testes
k6 run login_demo_volunteer.js          --summary-export ./modular/modular_login_vol_1.json
k6 run login_demo_volunteer.js          --summary-export ./modular/modular_login_vol_2.json
k6 run login_demo_volunteer.js          --summary-export ./modular/modular_login_vol_3.json


echo "Second Tests : aprox 10 min "
k6 run admin_login_get_themes.js        --summary-export ./modular/modular_admin_themes_1.json
k6 run admin_login_get_themes.js        --summary-export ./modular/modular_admin_themes_2.json
k6 run admin_login_get_themes.js        --summary-export ./modular/modular_admin_themes_3.json


echo "Third Tests : aprox 10 min "
k6 run register_volunteer_multipart.js  --summary-export ./modular/modular_register_vol_1.json
k6 run register_volunteer_multipart.js  --summary-export ./modular/modular_register_vol_2.json
k6 run register_volunteer_multipart.js  --summary-export ./modular/modular_register_vol_3.json