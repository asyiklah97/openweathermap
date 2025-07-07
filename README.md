# openweathermap
1. Run redis:
   docker run -d -p 6379:6379 --name rate-limit redis

2. Go to http://localhost:8080/data/2.5/weather?countryCode={country code}&city={city}&state={state}&appid={api key}
<br>
   <br> Available API keys:
   <br> a28ae42703b8ca82b605807ce9f7b89e
   <br> c282fc7b2c46abe3e2c994cfb57b5873
   <br> 4557160042b4140d3f203916a2860718
   <br> 1ad94ffe531f6277f996a2a801c662da
   <br> 314e8288634543dcc5e9bac7a1327214
   <br>
   <br>State is only for US
