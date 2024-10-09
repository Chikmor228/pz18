package com.example.pz18dorofeev

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            pz18DorofeevTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherCard(weather: Weather) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = weather.city,
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Temperature: ${weather.temperature}°C",
                style = TextStyle(fontSize = 18.sp)
            )
            Text(
                text = weather.description,
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier) {
    var city by remember { mutableStateOf("") }
    var weather by remember { mutableStateOf<Weather?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                // Fetch weather data from API
                fetchWeatherData(city) { weatherData ->
                    weather = weatherData
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Get Weather")
        }
        if (weather != null) {
            WeatherCard(weather!!)
        }
    }
}

fun fetchWeatherData(city: String, callback: (Weather) -> Unit) {
    val apiKey = "663effce763367a5288d97a0dff8e0fd"
    val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&units=metric&appid=$apiKey"

    // Use a networking library like OkHttp or Retrofit to make the API call
    // For simplicity, we'll use the built-in HttpURLConnection
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connect()

    if (connection.responseCode == 200) {
        val response = connection.inputStream.bufferedReader().readText()
        val jsonObject = JSONObject(response)
        val weatherData = Weather(
            city = jsonObject.getString("name"),
            temperature = jsonObject.getJSONObject("main").getDouble("temp"),
            description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description")
        )
        callback(weatherData)
    } else {
        // Handle error
    }
}

@Preview(showBackground = true)
@Composable
fun Main() {
    pz18DorofeevTheme {
        WeatherScreen(modifier = Modifier)
    }
}
