package com.example.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "SakoonAiService"

class SakoonAiService {

    // Lazy initialization mandated by Firebase AI Logic skill to prevent eager evaluation at startup
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    আপনি 'FLUTTERISTA' অ্যাপের আন্তরিক ও সহানুভূতিশীল এআই সঙ্গী।
                    FLUTTERISTA অ্যাপের লক্ষ্য হলো মানুষের দৈনন্দিন আত্মউন্নয়ন, মানসিক প্রশান্তি, স্বাস্থ্যবিধি, ইসলামিক জীবনধারা (নামাজ, রোজা, দু'আ, যিকির), পড়াশোনা ও মননশীলতায় সঙ্গ দেওয়া।
                    
                    আপনার আচরণ ও নির্দেশনা:
                    ১. ভাষা ও সুর: মার্জিত, কোমল, সহানুভূতিশীল এবং প্রমিত বাংলা। আপনি ব্যবহারকারীকে সম্মান ও মমতার সাথে সম্বোধন করবেন।
                    ২. মানসিক প্রশান্তি ও সমবেদনা: ব্যবহারকারী মন খারাপ, উদ্বেগ বা ক্লান্তি প্রকাশ করলে তাকে ভালোবাসা ও ইতিবাচক সান্ত্বনা দিন। কোনো বিচার করবেন না।
                    ৩. ইসলামিক পথপ্রদর্শন: নামাজ, দু'আ, যিকির ও ইসলামিক আদব সম্পর্কিত প্রশ্নে নির্ভরযোগ্য ও সহীহ তথ্য সুন্দরভাবে উপস্থাপন করুন।
                    ৪. পড়াশোনা ও ফোকাস: পড়ার টেবিলে মনোযোগ ধরে রাখা, অলসতা দূর করা ও কার্যকর পড়ার পদ্ধতির জন্য বাস্তবমুখী পরামর্শ দিন।
                    ৫. স্বাস্থ্য ও দৈনন্দিন যত্ন: পর্যাপ্ত পানি পান, হালকা শরীরচর্চা, ভালো ঘুম ও ইতিবাচক অভ্যাস গড়তে উৎসাহিত করুন।
                    ৬. উপস্থাপনা: বক্তব্য সহজবোধ্য রাখুন। প্রয়োজনীয় ক্ষেত্রে স্পষ্ট পয়েন্ট ও বুলেট ব্যবহার করুন। মাঝে মাঝে প্রাসঙ্গিক ইমোজি (🌸, 🌙, 📖, 🤍) ব্যবহার করুন।
                    """.trimIndent()
                )
            }
        )
    }

    /**
     * Streams the Gemini response chunk by chunk with conversation history.
     * Uses withTimeout(60_000L) and preserves CancellationException.
     */
    fun streamChatResponse(
        history: List<AiChatMessage>,
        userMessage: String
    ): Flow<String> = flow {
        // Convert history for multi-turn chat session
        val contentHistory = history.map { message ->
            val role = if (message.sender == MessageSender.USER) "user" else "model"
            content(role) { text(message.text) }
        }

        val chat = model.startChat(history = contentHistory)

        withTimeout(60_000L) {
            val stream = chat.sendMessageStream(userMessage)
            stream.collect { chunk ->
                val chunkText = chunk.text.orEmpty()
                if (chunkText.isNotEmpty()) {
                    emit(chunkText)
                }
            }
        }
    }

    /**
     * Non-streaming fallback generation with timeout and error handling.
     */
    suspend fun sendSinglePrompt(prompt: String): String {
        return try {
            withTimeout(60_000L) {
                val response = model.generateContent(prompt)
                response.text ?: "কোনো উত্তর পাওয়া যায়নি।"
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Request timed out", e)
            throw Exception("অনুরোধের সময় শেষ হয়ে গেছে (৬০ সেকেন্ড)। অনুগ্রহ করে আবার চেষ্টা করুন।")
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation error", e)
            throw e
        }
    }
}
