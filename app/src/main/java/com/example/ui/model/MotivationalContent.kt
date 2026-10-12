package com.example.ui.model

data class MotivationalQuote(
    val quote: String,
    val author: String,
    val context: String
)

data class UrgeTip(
    val title: String,
    val timeMinutes: Int,
    val iconEmoji: String,
    val actionText: String,
    val explanation: String
)

object MotivationalContent {
    val QUOTES = listOf(
        MotivationalQuote(
            quote = "Between stimulus and response there is a space. In that space is our power to choose our response.",
            author = "Viktor E. Frankl",
            context = "Self-Regulation"
        ),
        MotivationalQuote(
            quote = "He who conquers himself is the mightiest warrior.",
            author = "Confucius",
            context = "Inner Strength"
        ),
        MotivationalQuote(
            quote = "You have power over your mind - not outside events. Realize this, and you will find strength.",
            author = "Marcus Aurelius",
            context = "Stoic Resilience"
        ),
        MotivationalQuote(
            quote = "An urge is merely an electrical wave in your brain. It peaks in 15 minutes, then subsides.",
            author = "Neuroscience Axiom",
            context = "Urge Surfing"
        ),
        MotivationalQuote(
            quote = "We do not rise to the level of our goals. We fall to the level of our systems.",
            author = "James Clear",
            context = "Habit Formation"
        ),
        MotivationalQuote(
            quote = "Freedom is not the lack of desire; freedom is the sovereignty over which desires you obey.",
            author = "Epictetus",
            context = "Sovereignty"
        ),
        MotivationalQuote(
            quote = "Every time you resist a trigger, you physically prune the old neural circuit and strengthen the new one.",
            author = "Dr. Anna Lembke",
            context = "Brain Plasticity"
        ),
        MotivationalQuote(
            quote = "The pain of discipline is ounces. The pain of regret is tons.",
            author = "Jim Rohn",
            context = "Discipline"
        )
    )

    val URGE_TIPS = listOf(
        UrgeTip(
            title = "The 15-Minute Rule",
            timeMinutes = 15,
            iconEmoji = "⏱️",
            actionText = "Delay reaction by 15 mins",
            explanation = "Neurobiology shows impulse spikes last between 10 to 15 minutes. Tell yourself: 'I will decide in 15 minutes.' Set a timer and walk away."
        ),
        UrgeTip(
            title = "Cold Water Dive Reflex",
            timeMinutes = 2,
            iconEmoji = "🧊",
            actionText = "Splash cold water on face",
            explanation = "Activating the mammalian dive reflex by splashing cold water on your temples and eyes instantly drops your heart rate and parasympathetically soothes the nervous system."
        ),
        UrgeTip(
            title = "Physical Kinetic Discharge",
            timeMinutes = 3,
            iconEmoji = "💪",
            actionText = "Do 20 pushups or jumping jacks",
            explanation = "Redirect restless adrenaline and cortisol into muscular contraction. Moving large muscle groups burns off restless tension immediately."
        ),
        UrgeTip(
            title = "Change the Environment",
            timeMinutes = 5,
            iconEmoji = "🚪",
            actionText = "Exit the room immediately",
            explanation = "Your brain links cues to physical places. Stepping outside, walking into the living room, or turning on bright lights breaks the habitual loop."
        ),
        UrgeTip(
            title = "Drink a Large Glass of Ice Water",
            timeMinutes = 2,
            iconEmoji = "💧",
            actionText = "Drink 500ml cold water",
            explanation = "Physical sensation of cold swallowing grounds your vagus nerve and interrupts the urge trance."
        ),
        UrgeTip(
            title = "5-4-3-2-1 Sensory Grounding",
            timeMinutes = 4,
            iconEmoji = "👁️",
            actionText = "Engage all 5 senses now",
            explanation = "Notice 5 things you can see, 4 you can touch, 3 you can hear, 2 you can smell, and 1 you can taste. Pulls brain back into the present reality."
        )
    )
}
