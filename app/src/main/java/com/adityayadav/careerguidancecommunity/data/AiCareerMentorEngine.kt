package com.adityayadav.careerguidancecommunity.data

import com.adityayadav.careerguidancecommunity.data.model.AiCareerMilestone
import com.adityayadav.careerguidancecommunity.data.model.AiCareerPlan
import com.adityayadav.careerguidancecommunity.data.model.Profile
import com.adityayadav.careerguidancecommunity.data.model.UserProgress

/**
 * Intelligent on-device recommendation & mentorship engine that synthesizes
 * user profile, quiz assessment answers, and progression metrics into a
 * structured, high-impact career roadmap.
 */
object AiCareerMentorEngine {

    fun generatePlan(
        profile: Profile,
        scores: Map<String, Int>,
        progress: UserProgress
    ): AiCareerPlan {
        val topCategory = if (scores.isNotEmpty()) {
            scores.maxByOrNull { it.value }?.key ?: "Engineering"
        } else if (profile.goal.isNotBlank()) {
            inferCategoryFromGoal(profile.goal)
        } else {
            "Engineering"
        }

        val maxScore = scores[topCategory] ?: 24
        val matchPercentage = if (scores.isNotEmpty()) {
            ((maxScore.toFloat() / 32f) * 100).toInt().coerceIn(75, 98)
        } else {
            82
        }

        val userName = if (profile.name.isNotBlank()) profile.name else "Future Leader"

        return when (topCategory) {
            "Design" -> createDesignPlan(userName, matchPercentage)
            "Management" -> createManagementPlan(userName, matchPercentage)
            "Science" -> createSciencePlan(userName, matchPercentage)
            "Entrepreneurship" -> createEntrepreneurshipPlan(userName, matchPercentage)
            else -> createEngineeringPlan(userName, matchPercentage)
        }
    }

    private fun inferCategoryFromGoal(goal: String): String {
        val lower = goal.lowercase()
        return when {
            lower.contains("startup") || lower.contains("founder") || lower.contains("entrepreneur") || lower.contains("venture") -> "Entrepreneurship"
            lower.contains("business") || lower.contains("management") || lower.contains("mba") || lower.contains("finance") || lower.contains("consulting") -> "Management"
            lower.contains("design") || lower.contains("ux") || lower.contains("ui") || lower.contains("art") -> "Design"
            lower.contains("science") || lower.contains("research") || lower.contains("physics") || lower.contains("biology") -> "Science"
            else -> "Engineering"
        }
    }

    private fun createEngineeringPlan(userName: String, matchPercentage: Int): AiCareerPlan {
        return AiCareerPlan(
            careerTitle = "Software & Systems Innovator",
            careerCategory = "Engineering",
            matchPercentage = matchPercentage,
            executiveSummary = "Hey $userName, your analytical instincts and aptitude for structured problem-solving strongly align with modern Engineering & Technology. You thrive at building scalable systems, decomposing hard mathematical problems, and turning ideas into functioning software.",
            keyStrengths = listOf(
                "System architecture & algorithmic thinking",
                "Logical problem decomposition",
                "Hands-on technical implementation",
                "Rapid technical tooling adoption"
            ),
            growthAreas = listOf(
                "Open-source code collaboration & Git workflows",
                "Production system design & scalability",
                "Cross-disciplinary technical communication"
            ),
            targetEntranceExams = listOf("JEE Advanced", "JEE Main", "BITSAT", "GATE CS"),
            recommendedSkills = listOf("Kotlin & Modern Android", "Data Structures & Algorithms", "System Design", "Cloud Infrastructure (AWS/GCP)"),
            portfolioProjects = listOf(
                "High-performance mobile application with offline-first persistence",
                "Distributed REST & GraphQL API backend",
                "Open-source developer utility with continuous integration"
            ),
            milestones = listOf(
                AiCareerMilestone(
                    id = "eng_1",
                    title = "Master Data Structures & Algorithmic Patterns",
                    category = "Core Competencies",
                    description = "Consistently solve array, tree, and dynamic programming challenges. Focus on time and space complexity tradeoffs.",
                    priority = "Essential",
                    estimatedTimeline = "Month 1-2",
                    actionRoute = "quiz"
                ),
                AiCareerMilestone(
                    id = "eng_2",
                    title = "Architect & Ship a Full-Featured Compose App",
                    category = "Portfolio Projects",
                    description = "Build a multi-screen Kotlin application demonstrating clean architecture, state management, and modern UX.",
                    priority = "Essential",
                    estimatedTimeline = "Month 2-3",
                    actionRoute = "colleges"
                ),
                AiCareerMilestone(
                    id = "eng_3",
                    title = "Benchmark Top Technical Institutes & Entrance Cutoffs",
                    category = "Exams & Admissions",
                    description = "Compare NIRF top engineering institutes (IITs, NITs, BITS) and establish structured mock exam schedules.",
                    priority = "High",
                    estimatedTimeline = "Month 3-4",
                    actionRoute = "compare"
                ),
                AiCareerMilestone(
                    id = "eng_4",
                    title = "Apply for Merit & Technology Grants",
                    category = "Scholarships",
                    description = "Track and submit applications for top corporate & national scholarships such as Reliance Foundation & AICTE Pragati.",
                    priority = "High",
                    estimatedTimeline = "Month 4-5",
                    actionRoute = "scholarships"
                ),
                AiCareerMilestone(
                    id = "eng_5",
                    title = "Participate in Developer Communities & Hackathons",
                    category = "Industry Exposure",
                    description = "Join peer AMAs, collaborate on student hackathons, and seek code reviews from verified industry mentors.",
                    priority = "Medium",
                    estimatedTimeline = "Month 5-6",
                    actionRoute = "community"
                )
            )
        )
    }

    private fun createDesignPlan(userName: String, matchPercentage: Int): AiCareerPlan {
        return AiCareerPlan(
            careerTitle = "Product Designer & UX Strategist",
            careerCategory = "Design",
            matchPercentage = matchPercentage,
            executiveSummary = "Hey $userName, your innate creative empathy and visual sensitivity make you a natural for Product & UX Design. You possess an instinct for how interfaces feel, how users think, and how aesthetic harmony elevates human interaction.",
            keyStrengths = listOf(
                "Empathy-led user research & psychology",
                "Visual design hierarchy & color harmony",
                "Interaction prototyping & user flow mapping",
                "Creative synthesis of ambiguous briefs"
            ),
            growthAreas = listOf(
                "Accessible design systems (WCAG compliance)",
                "Quantitative usability metrics (A/B testing, SUS)",
                "Design handoff & design-to-code collaboration"
            ),
            targetEntranceExams = listOf("UCEED", "CEED", "NID DAT Prelims", "NIFT Entrance"),
            recommendedSkills = listOf("Figma & Design Tokens", "User Research & Heuristic Analysis", "Interactive Prototyping", "Information Architecture"),
            portfolioProjects = listOf(
                "End-to-end mobile UX case study with user interview artifacts",
                "Scalable Material 3 design token system library",
                "Redesign of a complex civic or healthcare digital service"
            ),
            milestones = listOf(
                AiCareerMilestone(
                    id = "des_1",
                    title = "Publish 3 Deep UX Case Studies",
                    category = "Portfolio Projects",
                    description = "Document problem statements, user personas, wireframes, iteration trade-offs, and final interactive prototypes.",
                    priority = "Essential",
                    estimatedTimeline = "Month 1-3",
                    actionRoute = "colleges"
                ),
                AiCareerMilestone(
                    id = "des_2",
                    title = "UCEED & NID Design Aptitude Preparation",
                    category = "Exams & Admissions",
                    description = "Practice visual drawing, observational sensitivity, spatial reasoning, and design problem solving under timed tests.",
                    priority = "High",
                    estimatedTimeline = "Month 2-4",
                    actionRoute = "compare"
                ),
                AiCareerMilestone(
                    id = "des_3",
                    title = "Conduct 5 Guided Usability Testing Sessions",
                    category = "Core Competencies",
                    description = "Draft user testing scripts, observe participants navigating your prototypes, and synthesize behavioral friction logs.",
                    priority = "High",
                    estimatedTimeline = "Month 3-5",
                    actionRoute = "community"
                ),
                AiCareerMilestone(
                    id = "des_4",
                    title = "Apply for Creative Arts & Design Fellowships",
                    category = "Scholarships",
                    description = "Identify institutional design grants and government higher education fee waiver schemes for top design schools.",
                    priority = "Medium",
                    estimatedTimeline = "Month 4-6",
                    actionRoute = "scholarships"
                )
            )
        )
    }

    private fun createManagementPlan(userName: String, matchPercentage: Int): AiCareerPlan {
        return AiCareerPlan(
            careerTitle = "Business Strategist & Product Leader",
            careerCategory = "Management",
            matchPercentage = matchPercentage,
            executiveSummary = "Hey $userName, you shine at synthesizing cross-functional priorities, aligning teams, and driving commercial outcomes. You have natural leadership presence, communication poise, and strategic clarity.",
            keyStrengths = listOf(
                "Cross-functional team coordination",
                "Persuasive presentation & stakeholder alignment",
                "Strategic prioritization & roadmapping",
                "Market analysis & business model synthesis"
            ),
            growthAreas = listOf(
                "Quantitative financial modeling & unit economics",
                "Data analytics & SQL query formulation",
                "Managing organizational conflict & negotiations"
            ),
            targetEntranceExams = listOf("CAT", "XAT", "IPMAT (IIM Indore/Rohtak)", "GMAT"),
            recommendedSkills = listOf("Financial Statement Analysis", "Agile Product Management", "Data Storytelling (SQL/BI)", "Business Case Modeling"),
            portfolioProjects = listOf(
                "Go-To-Market expansion teardown for a unicorn business model",
                "Comprehensive unit economics model with sensitivity analysis",
                "Product strategy memorandum prioritizing 3 growth wedges"
            ),
            milestones = listOf(
                AiCareerMilestone(
                    id = "mgmt_1",
                    title = "Complete a Strategic Market Teardown",
                    category = "Portfolio Projects",
                    description = "Deconstruct a business model's CAC, LTV, moat, and competitive threats into an executive presentation.",
                    priority = "Essential",
                    estimatedTimeline = "Month 1-2",
                    actionRoute = "colleges"
                ),
                AiCareerMilestone(
                    id = "mgmt_2",
                    title = "IPMAT & CAT Foundation Quantitative Mastery",
                    category = "Exams & Admissions",
                    description = "Target quantitative aptitude, logical reasoning, and data interpretation with timed benchmark mocks.",
                    priority = "High",
                    estimatedTimeline = "Month 2-4",
                    actionRoute = "compare"
                ),
                AiCareerMilestone(
                    id = "mgmt_3",
                    title = "Lead a Student Initiative or Case Competition Team",
                    category = "Leadership & Practice",
                    description = "Take operational ownership of a student forum, case challenge, or social impact project to demonstrate real impact.",
                    priority = "High",
                    estimatedTimeline = "Month 3-5",
                    actionRoute = "community"
                ),
                AiCareerMilestone(
                    id = "mgmt_4",
                    title = "Apply for Corporate Leadership Scholarships",
                    category = "Scholarships",
                    description = "Identify merit-based management grants including Aditya Birla Scholarships and OP Jindal Engineering/Management awards.",
                    priority = "High",
                    estimatedTimeline = "Month 4-6",
                    actionRoute = "scholarships"
                )
            )
        )
    }

    private fun createSciencePlan(userName: String, matchPercentage: Int): AiCareerPlan {
        return AiCareerPlan(
            careerTitle = "Scientific Researcher & Deep-Tech Explorer",
            careerCategory = "Science",
            matchPercentage = matchPercentage,
            executiveSummary = "Hey $userName, your innate curiosity and dedication to rigorous first-principles thinking define your mindset. You are captivated by understanding how the universe works, testing hypotheses, and pioneering breakthroughs.",
            keyStrengths = listOf(
                "Empirical hypothesis formulation & testing",
                "Critical literature evaluation",
                "Mathematical & statistical rigor",
                "Patience for deep iterative investigation"
            ),
            growthAreas = listOf(
                "Academic scientific writing & LaTeX formatting",
                "Computational data modeling (Python/SciPy)",
                "Grant proposal and research funding drafting"
            ),
            targetEntranceExams = listOf("NEST (NISER/CEBS)", "IAT (IISERs)", "IIT-JAM", "CSIR NET"),
            recommendedSkills = listOf("Statistical Modeling (Python/R)", "LaTeX & Scientific Documentation", "Experimental Data Analysis", "Research Methodology"),
            portfolioProjects = listOf(
                "Reproduced computational paper on statistical physical dynamics",
                "Genomic / Climate exploratory analysis with published Jupyter notebook",
                "Literature review on emerging clean energy materials"
            ),
            milestones = listOf(
                AiCareerMilestone(
                    id = "sci_1",
                    title = "Author an Empirical Research Synthesis",
                    category = "Research Output",
                    description = "Select an open scientific problem, survey 15 recent papers, and synthesize unresolved experimental tensions.",
                    priority = "Essential",
                    estimatedTimeline = "Month 1-3",
                    actionRoute = "colleges"
                ),
                AiCareerMilestone(
                    id = "sci_2",
                    title = "Prepare for IISER IAT & NEST Examinations",
                    category = "Exams & Admissions",
                    description = "Master multi-disciplinary physics, chemistry, mathematics, and biology concepts for premier research institutes.",
                    priority = "Essential",
                    estimatedTimeline = "Month 2-4",
                    actionRoute = "compare"
                ),
                AiCareerMilestone(
                    id = "sci_3",
                    title = "Secure Summer Research Fellowship (IASc / INSA)",
                    category = "Fellowships",
                    description = "Draft research statements and apply to summer research programs at IISc, TIFR, or IIT research labs.",
                    priority = "High",
                    estimatedTimeline = "Month 3-5",
                    actionRoute = "scholarships"
                ),
                AiCareerMilestone(
                    id = "sci_4",
                    title = "Apply for INSPIRE & Kishore Vaigyanik Grants",
                    category = "Scholarships",
                    description = "Register for Department of Science & Technology (DST) scholarship funding for pure science research aspirants.",
                    priority = "High",
                    estimatedTimeline = "Month 4-6",
                    actionRoute = "scholarships"
                )
            )
        )
    }

    private fun createEntrepreneurshipPlan(userName: String, matchPercentage: Int): AiCareerPlan {
        return AiCareerPlan(
            careerTitle = "Venture Founder & Innovation Catalyst",
            careerCategory = "Entrepreneurship",
            matchPercentage = matchPercentage,
            executiveSummary = "Hey $userName, you have the grit to forge new pathways, spot latent market inefficiencies, and build ventures from the ground up. You are resourceful, comfortable with ambiguity, and driven by high-leverage impact.",
            keyStrengths = listOf(
                "Zero-to-one resourcefulness & bias for action",
                "Customer discovery & problem validation",
                "Speed in rapid prototyping & hypothesis testing",
                "Resilience in the face of setbacks"
            ),
            growthAreas = listOf(
                "Customer acquisition cost (CAC) & unit economics",
                "Cap table management & venture fundraising terms",
                "Recruiting and managing high-performing talent"
            ),
            targetEntranceExams = listOf("IPMAT / CAT", "Y Combinator / Startup Incubators", "NSRCEL / E-Cell Challenges"),
            recommendedSkills = listOf("Customer Problem Discovery", "No-Code / Rapid Prototyping", "Investor Pitch Deck Design", "Financial Runaway Modeling"),
            portfolioProjects = listOf(
                "Functional MVP launched to 50 real active users",
                "Comprehensive Investor Pitch Deck with unit economic model",
                "Customer discovery report based on 25 in-depth user interviews"
            ),
            milestones = listOf(
                AiCareerMilestone(
                    id = "ent_1",
                    title = "Conduct 20 Customer Pain-Point Interviews",
                    category = "Problem Validation",
                    description = "Speak directly to target users without selling. Identify recurring frustrations they actively pay money or time to solve.",
                    priority = "Essential",
                    estimatedTimeline = "Month 1-2",
                    actionRoute = "community"
                ),
                AiCareerMilestone(
                    id = "ent_2",
                    title = "Launch MVP and Acquire First 50 Users",
                    category = "Product Execution",
                    description = "Build the smallest possible solution that solves the core problem and measure retention relentlessly.",
                    priority = "Essential",
                    estimatedTimeline = "Month 2-4",
                    actionRoute = "colleges"
                ),
                AiCareerMilestone(
                    id = "ent_3",
                    title = "Draft 12-Month Financial Unit Economics Model",
                    category = "Finance & Strategy",
                    description = "Project cash runway, pricing tiers, gross margin, and customer acquisition costs under realistic scenarios.",
                    priority = "High",
                    estimatedTimeline = "Month 3-5",
                    actionRoute = "compare"
                ),
                AiCareerMilestone(
                    id = "ent_4",
                    title = "Apply for Startup India Seed Grants & College Incubators",
                    category = "Grants & Funding",
                    description = "Submit applications for non-dilutive government grants, college E-Cell seed funds, and angel angel competitions.",
                    priority = "High",
                    estimatedTimeline = "Month 4-6",
                    actionRoute = "scholarships"
                )
            )
        )
    }
}
