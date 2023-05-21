package com.example.palliativecare.ui.screen

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.palliativecare.controller.article.ArticleController
import com.example.palliativecare.controller.category.CategoryController
import com.example.palliativecare.model.Article
import com.example.palliativecare.model.Category
import com.example.palliativecare.model.User
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    articleController: ArticleController,
    categoryController: CategoryController
) {
    val image = remember { mutableStateOf<Uri?>(null) }
    val selectedTopic = remember { mutableStateOf<Category>(Category("1", "الكل")) }
    val topics = remember { mutableStateListOf<Category>() }
    var query by remember { mutableStateOf("") }
    val articles = remember { mutableStateListOf<Article>() }
    val coroutineScope = rememberCoroutineScope()
//    val searchQuery = remember { mutableStateOf("") }




    LaunchedEffect(Unit) {
        articles.addAll(articleController.getAllArticle())
    }

    LaunchedEffect(Unit) {
        topics.add(Category("1", "الكل"))
        topics.addAll(categoryController.getAllCategory())
    }

    val filteredArticles = articles.filter {
        it.title.contains(query, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        RoundedSearchBar(query = query, onQueryChange = { it -> query = it }, onSearchClick = {})
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(topics) { topic ->
                TopicItem(
                    selected = topic.id == selectedTopic.value.id,
                    text = topic.name,
                    onSelectedChange = {
                        selectedTopic.value = topic
                        if(selectedTopic.value.name == "الكل"){
                            coroutineScope.launch {
                                articles.clear()
                                articles.addAll(articleController.getAllArticle())
                            }
                        }else{
                            coroutineScope.launch {
                                articles.clear()
                                articles.addAll(articleController.getArticleByCategory(selectedTopic.value.id))
                            }
                        }
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredArticles) { article ->
                val doctorName = remember {
                    mutableStateOf("")
                }
                LaunchedEffect(Unit){
                    coroutineScope.launch {
                        doctorName.value = User.getUserByID(article.doctorId).first().name
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            navController.navigate("article_details_screen/${article.id}")
                        },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = article.picture,
                            contentDescription = "Profile picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .background(color = Color.Black.copy(alpha = 0.4f))
                                .fillMaxSize()
                        )
                        Text(
                            text = article.title,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        )

                        Text(
                            text = doctorName.value,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                        )
                        Text(
                            text = article.createdAt,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicItem(
    selected: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    onSelectedChange: () -> Unit
) {
    Surface(
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
        contentColor = if (selected) White else MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) Color.Transparent else MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.clickable { onSelectedChange() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundedSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .heightIn(min = 48.dp),
        placeholder = {
            Text(
                text = "بحث عن مقالة...",
                color = Color.Gray,
            )
        },
        singleLine = true,
        leadingIcon = {
            IconButton(
                onClick = { onQueryChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Clear",
                    tint = Color.Gray
                )
            }
        },
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearchClick() }
        ),
        shape = RoundedCornerShape(10.dp)
    )
}
