package com.moonring.ring.bean

class ArticleList{
    var list:List<Article>?=null
}
data class Article(
    val content_id: String = "",
    val title: String = "",
    val content: String = "",
    val category_name: String = "",
    val publish_time: String = "",
    var imgUrl:String = ""
)
