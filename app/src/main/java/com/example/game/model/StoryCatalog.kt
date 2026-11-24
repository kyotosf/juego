package com.example.game.model

import com.example.R
import com.example.ui.theme.ElectricWebCyan
import com.example.ui.theme.GoldenSunset
import com.example.ui.theme.SpiderCrimson
import com.example.ui.theme.VelvetRose

object StoryCatalog {

    val memories: List<CanonicalMemory> = listOf(
        CanonicalMemory(
            id = "mem_queens_rose",
            title = "La Rosa de Queens",
            chapterTag = "Capítulo 1",
            quote = "«Incluso a treinta metros de altura, mi sentido arácnido siempre apunta hacia ti, Isabella.»",
            letterBody = "Mi querida Isabella: aquel primer atardecer entendí que balancearse entre rascacielos era fácil comparado con encontrar palabras dignas de tu sonrisa. Esta rosa sobrevivió al viento solo porque llevaba tu nombre.",
            kind = HotspotKind.ROSE
        ),
        CanonicalMemory(
            id = "mem_sketchbook",
            title = "El Cuaderno de Bocetos",
            chapterTag = "Capítulo 1",
            quote = "«Dibujaste una ciudad entera, pero en cada margen aparecía nuestro pequeño universo.»",
            letterBody = "Cuando atrapé tu cuaderno en el aire, vi trazos de Nueva York iluminados con una calidez que ni el mejor atardecer de Manhattan podría imitar. Prometí cuidar cada página como cuido cada día contigo.",
            kind = HotspotKind.SKETCHBOOK
        ),
        CanonicalMemory(
            id = "mem_string_lights",
            title = "Guirnaldas en la Azotea",
            chapterTag = "Capítulo 2",
            quote = "«No hacen falta focos de estadio cuando basta una guirnalda tibia y tus ojos reflejándola.»",
            letterBody = "Encendí cada foco con un hilo de telaraña para que la azotea pareciera un pedazo de cielo privado. Si alguna noche la ciudad se siente fría, recuerda que aquí siempre hay luz para nosotros.",
            kind = HotspotKind.LANTERN
        ),
        CanonicalMemory(
            id = "mem_vinyl_song",
            title = "Nuestro Vinilo Secreto",
            chapterTag = "Capítulo 2",
            quote = "«Mientras giraba la aguja, el mundo dejó de correr y solo existió nuestro compás.»",
            letterBody = "Entre patrullajes y emergencias, esa canción se volvió nuestro refugio canónico. Cada acorde me recuerda que el verdadero superpoder es detener el tiempo cuando te abrazo.",
            kind = HotspotKind.VINYL
        ),
        CanonicalMemory(
            id = "mem_bridge_catch",
            title = "El Hilo del Destino",
            chapterTag = "Capítulo 3",
            quote = "«En otros universos el destino pesa; en el nuestro, mi red jamás te soltará, Isabella.»",
            letterBody = "Cuando los drones iluminaron el Puente de Brooklyn con chispas azules, no dudé ni un segundo. Mis dos manos lanzaron telaraña hacia el futuro, y ese futuro siempre eres tú.",
            kind = HotspotKind.DRONE
        ),
        CanonicalMemory(
            id = "mem_neon_promise",
            title = "Promesa Bajo la Lluvia Neón",
            chapterTag = "Capítulo 3",
            quote = "«Que tiemblen las tormentas: juntos somos más fuertes que cualquier evento canónico.»",
            letterBody = "El neón reflejado en el río East fue testigo de mi juramento más honesto: no importa cuán difícil sea la misión, siempre volveré sano y salvo a tus brazos.",
            kind = HotspotKind.ROSE
        ),
        CanonicalMemory(
            id = "mem_unmasked_truth",
            title = "Sin Máscara Ante Ti",
            chapterTag = "Capítulo 4",
            quote = "«Ser Spider-Man protege a la ciudad; quitarme la máscara contigo protege mi alma.»",
            letterBody = "Isabella, ante el mundo llevo secretos y cicatrices; ante ti solo soy el chico enamorado que te eligió mil veces antes de que existiera el traje.",
            kind = HotspotKind.RELIC_CAMERA
        ),
        CanonicalMemory(
            id = "mem_scarf_gift",
            title = "La Bufanda Carmesí",
            chapterTag = "Capítulo 4",
            quote = "«Roja como el traje, suave como tu voz en las madrugadas de invierno.»",
            letterBody = "Cada vez que el viento helado golpea los tejados, el recuerdo de tu abrazo abriga más que cualquier tela. Eres mi hogar en mitad de Manhattan.",
            kind = HotspotKind.RELIC_SCARF
        ),
        CanonicalMemory(
            id = "mem_constellation_isabella",
            title = "Constelación Isabella",
            chapterTag = "Capítulo 5",
            quote = "«Uní cinco estrellas con hilo dorado para que hasta el cielo sepa a quién pertenece mi corazón.»",
            letterBody = "Desde nuestro observatorio secreto, las constelaciones dejaron de ser mitos antiguos: ahora brillan dibujando tu nombre sobre toda la ciudad.",
            kind = HotspotKind.CONSTELLATION_STAR
        ),
        CanonicalMemory(
            id = "mem_eternal_letter",
            title = "Carta Canónica Eterna",
            chapterTag = "Epílogo",
            quote = "«En todos los universos posibles, en cada línea temporal, siempre te elegiría a ti, Isabella.»",
            letterBody = "Gracias por ser mi compañera de aventuras, mi calma después de cada batalla y la razón por la que esta historia tiene el mejor final canónico jamás escrito. Te amo infinitamente.",
            kind = HotspotKind.RELIC_LETTER
        )
    )

    val chapters: List<ChapterSpec> = listOf(
        ChapterSpec(
            number = 1,
            title = "Atardecer en Queens",
            subtitle = "El Primer Encuentro Canónico",
            locationName = "Tejados de Queens — Hora Dorada",
            backdropResId = R.drawable.img_rooftop_sunset_1795533350764,
            accentColor = GoldenSunset,
            sceneType = SceneType.WEB_SWING_ROOFTOPS,
            objectiveLabel = "Balancéate y recoge las 3 rosas y el cuaderno de Isabella",
            hotspots = listOf(
                WorldHotspot(
                    id = "c1_rose1",
                    label = "Rosa del Campanario",
                    xFraction = 0.20f,
                    yFraction = 0.42f,
                    kind = HotspotKind.ROSE,
                    unlocksMemoryId = "mem_queens_rose",
                    reactionText = "¡THWIP! Atrapaste una rosa fresca antes de que cayera de la cornisa."
                ),
                WorldHotspot(
                    id = "c1_rose2",
                    label = "Rosa del Mirador",
                    xFraction = 0.48f,
                    yFraction = 0.30f,
                    kind = HotspotKind.ROSE,
                    reactionText = "El viento dorado huele a primavera. ¡Otra rosa para Isabella!"
                ),
                WorldHotspot(
                    id = "c1_sketch",
                    label = "Cuaderno de Isabella",
                    xFraction = 0.72f,
                    yFraction = 0.50f,
                    kind = HotspotKind.SKETCHBOOK,
                    unlocksMemoryId = "mem_sketchbook",
                    reactionText = "¡Salvaste el cuaderno de bocetos de Isabella! Tiene dibujos de estrellas y telarañas."
                ),
                WorldHotspot(
                    id = "c1_rose3",
                    label = "Rosa del Balcón",
                    xFraction = 0.86f,
                    yFraction = 0.36f,
                    kind = HotspotKind.ROSE,
                    reactionText = "¡Las 3 rosas están completas! Isabella te espera junto a la barandilla."
                )
            ),
            dialogueBeats = listOf(
                DialogueBeat(
                    speaker = Speaker.NARRADOR,
                    text = "El cielo de Queens se tiñe de oro y carmesí. Con el cuaderno y las rosas a salvo, Spider-Man aterriza suavemente frente al balcón donde Isabella contempla la ciudad."
                ),
                DialogueBeat(
                    speaker = Speaker.ISABELLA,
                    text = "Sabía que ese silbido del viento solo podía traer a un héroe... ¿Por qué siempre apareces justo cuando mi corazón más necesita un respiro?"
                ),
                DialogueBeat(
                    speaker = Speaker.SPIDER_MAN,
                    text = "Mi sentido arácnido suele avisarme del peligro, Isabella... pero contigo aprendí que también vibra cuando estoy frente a algo extraordinario.",
                    choices = listOf(
                        StoryChoice(
                            id = "ch1_tender",
                            text = "Entregarle las rosas con una reverencia romántica",
                            traitTag = "Ternura",
                            bondDelta = 12,
                            courageDelta = 2,
                            tendernessDelta = 12,
                            witDelta = 4,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella sonríe con las mejillas encendidas: «Nadie en toda Nueva York sabe convertir un rescate en un poema como tú.»"
                        ),
                        StoryChoice(
                            id = "ch1_wit",
                            text = "«Servicio exprés de araña amiga: cero cargos de envío para ti.»",
                            traitTag = "Complicidad",
                            bondDelta = 10,
                            courageDelta = 4,
                            tendernessDelta = 5,
                            witDelta = 12,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella ríe con dulzura: «Definitivamente eres mi superhéroe favorito de todo el multiverso.»"
                        ),
                        StoryChoice(
                            id = "ch1_courage",
                            text = "«Prometo proteger tu sonrisa incluso contra la peor tormenta.»",
                            traitTag = "Valentía",
                            bondDelta = 11,
                            courageDelta = 12,
                            tendernessDelta = 7,
                            witDelta = 2,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella toma tu mano enguantada: «Y yo prometo ser siempre tu lugar seguro, estés donde estés.»"
                        )
                    )
                )
            ),
            completionMemoryId = "mem_queens_rose"
        ),
        ChapterSpec(
            number = 2,
            title = "La Azotea Secreta",
            subtitle = "Luces Entre Telarañas",
            locationName = "Terraza Privada de Manhattan",
            backdropResId = R.drawable.img_rooftop_sunset_1795533350764,
            accentColor = VelvetRose,
            sceneType = SceneType.ROOFTOP_TERRACE_EXPLORE,
            objectiveLabel = "Prepara la cita: enciende las luces, el vinilo y el chocolate caliente",
            hotspots = listOf(
                WorldHotspot(
                    id = "c2_lights_left",
                    label = "Guirnalda Oeste",
                    xFraction = 0.22f,
                    yFraction = 0.34f,
                    kind = HotspotKind.LANTERN,
                    unlocksMemoryId = "mem_string_lights",
                    reactionText = "Las bombillas cálidas iluminan la madera de la pérgola con un brillo íntimo."
                ),
                WorldHotspot(
                    id = "c2_lights_right",
                    label = "Guirnalda del Horizonte",
                    xFraction = 0.78f,
                    yFraction = 0.36f,
                    kind = HotspotKind.LANTERN,
                    reactionText = "¡Click! La segunda línea de luces enmarca el Empire State al fondo."
                ),
                WorldHotspot(
                    id = "c2_vinyl",
                    label = "Tocadiscos Vintage",
                    xFraction = 0.36f,
                    yFraction = 0.64f,
                    kind = HotspotKind.VINYL,
                    unlocksMemoryId = "mem_vinyl_song",
                    reactionText = "Suena una melodía cálida en vinilo. El ambiente se vuelve pura magia."
                ),
                WorldHotspot(
                    id = "c2_cocoa",
                    label = "Chocolate y Pétalos",
                    xFraction = 0.66f,
                    yFraction = 0.68f,
                    kind = HotspotKind.HOT_CHOCOLATE,
                    reactionText = "Dos tazas humeantes rodeadas de pétalos rojos esperan sobre la mesa."
                )
            ),
            dialogueBeats = listOf(
                DialogueBeat(
                    speaker = Speaker.ISABELLA,
                    text = "Esta azotea parece sacada de un sueño... Entre tantos edificios gigantes, creaste un rincón donde el mundo entero se detiene solo para nosotros."
                ),
                DialogueBeat(
                    speaker = Speaker.SPIDER_MAN,
                    text = "Quería que tuvieras una noche sin alarmas ni villanos, Isabella. Solo tú, el brillo de la ciudad y esta canción.",
                    choices = listOf(
                        StoryChoice(
                            id = "ch2_dance",
                            text = "Invitarla a bailar lento bajo las guirnaldas doradas",
                            traitTag = "Ternura",
                            bondDelta = 12,
                            courageDelta = 3,
                            tendernessDelta = 14,
                            witDelta = 3,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella apoya su frente junto a la tuya mientras giran despacio: «Ojalá esta canción durara mil años.»"
                        ),
                        StoryChoice(
                            id = "ch2_toast",
                            text = "Brindar con chocolate caliente por cada aventura juntos",
                            traitTag = "Complicidad",
                            bondDelta = 10,
                            courageDelta = 5,
                            tendernessDelta = 8,
                            witDelta = 10,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella choca su taza con la tuya: «¡Por nosotros y por todas las azoteas que aún nos faltan!»"
                        )
                    )
                )
            ),
            completionMemoryId = "mem_vinyl_song"
        ),
        ChapterSpec(
            number = 3,
            title = "Tormenta en Brooklyn",
            subtitle = "El Rescate Canónico",
            locationName = "Puente de Brooklyn — Lluvia Neón",
            backdropResId = R.drawable.img_neon_bridge_1795533351464,
            accentColor = ElectricWebCyan,
            sceneType = SceneType.BRIDGE_DRONE_RESCUE,
            objectiveLabel = "Neutraliza los 4 drones con telarañas (Espacio / ¡THWIP!) y protege a Isabella",
            hotspots = listOf(
                WorldHotspot(
                    id = "c3_drone1",
                    label = "Dron Alfa",
                    xFraction = 0.25f,
                    yFraction = 0.28f,
                    kind = HotspotKind.DRONE,
                    reactionText = "¡THWIP! Primer dron envuelto en seda eléctrica y neutralizado."
                ),
                WorldHotspot(
                    id = "c3_drone2",
                    label = "Dron Beta",
                    xFraction = 0.50f,
                    yFraction = 0.22f,
                    kind = HotspotKind.DRONE,
                    reactionText = "¡Impacto directo! Los cables del puente vuelven a brillar sin interferencia."
                ),
                WorldHotspot(
                    id = "c3_drone3",
                    label = "Dron Gamma",
                    xFraction = 0.76f,
                    yFraction = 0.30f,
                    kind = HotspotKind.DRONE,
                    unlocksMemoryId = "mem_bridge_catch",
                    reactionText = "¡Desarmado en el aire! Solo queda el dron líder cerca de la pasarela."
                ),
                WorldHotspot(
                    id = "c3_drone4",
                    label = "Dron Centinela",
                    xFraction = 0.62f,
                    yFraction = 0.48f,
                    kind = HotspotKind.DRONE,
                    unlocksMemoryId = "mem_neon_promise",
                    reactionText = "¡Rescate completado! Atrapas a Isabella en pleno salto entre luces neón."
                )
            ),
            dialogueBeats = listOf(
                DialogueBeat(
                    speaker = Speaker.NARRADOR,
                    text = "Las gotas de lluvia reflejan el neón cian sobre el puente. Con un último impulso acrobático, Spider-Man sujeta a Isabella contra su pecho y aterriza a salvo."
                ),
                DialogueBeat(
                    speaker = Speaker.ISABELLA,
                    text = "Por un segundo creí que el puente cedería... pero sentí tu telaraña y supe que nada malo podía pasarme contigo aquí."
                ),
                DialogueBeat(
                    speaker = Speaker.SPIDER_MAN,
                    text = "Dicen que todo Spider-Man sufre una pérdida inevitable en el puente... pero contigo acabo de reescribir la historia canónica.",
                    choices = listOf(
                        StoryChoice(
                            id = "ch3_hold",
                            text = "Abrazarla fuerte bajo la lluvia neón y calmar su pulso",
                            traitTag = "Ternura",
                            bondDelta = 12,
                            courageDelta = 6,
                            tendernessDelta = 14,
                            witDelta = 2,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella cierra los ojos escuchando tu latido: «Nunca me sueltes, mi héroe.»"
                        ),
                        StoryChoice(
                            id = "ch3_defy",
                            text = "«Que el universo tome nota: a Isabella nadie la toca.»",
                            traitTag = "Valentía",
                            bondDelta = 12,
                            courageDelta = 15,
                            tendernessDelta = 6,
                            witDelta = 4,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella te mira admirada: «Contigo al lado, hasta las tormentas parecen pequeñas.»"
                        )
                    )
                )
            ),
            completionMemoryId = "mem_bridge_catch"
        ),
        ChapterSpec(
            number = 4,
            title = "Santuario de Recuerdos",
            subtitle = "Detrás de la Máscara",
            locationName = "Refugio Secreto sobre el SoHo",
            backdropResId = R.drawable.img_neon_bridge_1795533351464,
            accentColor = SpiderCrimson,
            sceneType = SceneType.MEMORY_GALLERY_REVEAL,
            objectiveLabel = "Examina las 4 reliquias canónicas para compartir tu verdad con Isabella",
            hotspots = listOf(
                WorldHotspot(
                    id = "c4_camera",
                    label = "Cámara Antigua",
                    xFraction = 0.20f,
                    yFraction = 0.56f,
                    kind = HotspotKind.RELIC_CAMERA,
                    unlocksMemoryId = "mem_unmasked_truth",
                    reactionText = "La vieja cámara guarda la primera foto borrosa de Isabella sonriendo bajo un farol."
                ),
                WorldHotspot(
                    id = "c4_scarf",
                    label = "Bufanda Carmesí",
                    xFraction = 0.42f,
                    yFraction = 0.44f,
                    kind = HotspotKind.RELIC_SCARF,
                    unlocksMemoryId = "mem_scarf_gift",
                    reactionText = "Una bufanda tejida a mano del mismo rojo brillante que el traje arácnido."
                ),
                WorldHotspot(
                    id = "c4_ticket",
                    label = "Boleto del Primer Cine",
                    xFraction = 0.64f,
                    yFraction = 0.58f,
                    kind = HotspotKind.RELIC_TICKET,
                    reactionText = "Un boleto conservado con fecha especial: «El día que olvidamos ver la película por mirarnos»."
                ),
                WorldHotspot(
                    id = "c4_letter",
                    label = "Carta Sin Enviar",
                    xFraction = 0.83f,
                    yFraction = 0.42f,
                    kind = HotspotKind.RELIC_LETTER,
                    reactionText = "En el sobre se lee con tinta dorada: «Para Isabella, mi verdad más valiosa»."
                )
            ),
            dialogueBeats = listOf(
                DialogueBeat(
                    speaker = Speaker.SPIDER_MAN,
                    text = "Isabella, cada una de estas reliquias demuestra que incluso antes de cada salto heroico, tú ya eras el centro de mi mundo. Hoy no quiero que haya máscaras entre nosotros."
                ),
                DialogueBeat(
                    speaker = Speaker.ISABELLA,
                    text = "Con o sin máscara, siempre reconocí tu alma por la forma en que cuidas cada pequeño detalle...",
                    choices = listOf(
                        StoryChoice(
                            id = "ch4_heart",
                            text = "Confesarle que ella es la verdadera fuerza detrás del héroe",
                            traitTag = "Ternura",
                            bondDelta = 14,
                            courageDelta = 6,
                            tendernessDelta = 15,
                            witDelta = 3,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella acaricia tu rostro con suavidad: «Entonces nunca tendrás que cargar la ciudad solo.»"
                        ),
                        StoryChoice(
                            id = "ch4_smile",
                            text = "«Solo te pido no revelar que despeinado pierdo puntos de estilo.»",
                            traitTag = "Complicidad",
                            bondDelta = 11,
                            courageDelta = 5,
                            tendernessDelta = 8,
                            witDelta = 14,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella te arregla el cabello riendo: «Así me gustas todavía más.»"
                        )
                    )
                )
            ),
            completionMemoryId = "mem_unmasked_truth"
        ),
        ChapterSpec(
            number = 5,
            title = "Observatorio Estelar",
            subtitle = "La Constelación Isabella",
            locationName = "Cúpula de Cristal — Medianoche",
            backdropResId = R.drawable.img_starlight_sanctuary_1795533352110,
            accentColor = GoldenSunset,
            sceneType = SceneType.STARLIGHT_CONSTELLATION,
            objectiveLabel = "Teje con hilo dorado las 5 estrellas para formar la Constelación Isabella",
            hotspots = listOf(
                WorldHotspot(
                    id = "c5_star1",
                    label = "Estrella I — Origen",
                    xFraction = 0.18f,
                    yFraction = 0.30f,
                    kind = HotspotKind.CONSTELLATION_STAR,
                    reactionText = "Primer destello tejido en el firmamento nocturno."
                ),
                WorldHotspot(
                    id = "c5_star2",
                    label = "Estrella II — Risa",
                    xFraction = 0.34f,
                    yFraction = 0.20f,
                    kind = HotspotKind.CONSTELLATION_STAR,
                    reactionText = "Un puente de seda luminosa une las dos primeras estrellas."
                ),
                WorldHotspot(
                    id = "c5_star3",
                    label = "Estrella III — Corazón",
                    xFraction = 0.50f,
                    yFraction = 0.34f,
                    kind = HotspotKind.CONSTELLATION_STAR,
                    unlocksMemoryId = "mem_constellation_isabella",
                    reactionText = "¡El centro de la constelación palpita con un brillo rubí!"
                ),
                WorldHotspot(
                    id = "c5_star4",
                    label = "Estrella IV — Promesa",
                    xFraction = 0.68f,
                    yFraction = 0.22f,
                    kind = HotspotKind.CONSTELLATION_STAR,
                    reactionText = "Los faroles flotantes ascienden celebrando el cuarto trazo estelar."
                ),
                WorldHotspot(
                    id = "c5_star5",
                    label = "Estrella V — Eternidad",
                    xFraction = 0.82f,
                    yFraction = 0.36f,
                    kind = HotspotKind.CONSTELLATION_STAR,
                    unlocksMemoryId = "mem_eternal_letter",
                    reactionText = "¡La Constelación ISABELLA resplandece sobre toda Nueva York!"
                )
            ),
            dialogueBeats = listOf(
                DialogueBeat(
                    speaker = Speaker.NARRADOR,
                    text = "Decenas de faroles dorados flotan hacia las estrellas mientras la telaraña luminosa dibuja una constelación eterna sobre Manhattan."
                ),
                DialogueBeat(
                    speaker = Speaker.ISABELLA,
                    text = "Mira el cielo... Todo lo que vivimos esta noche brilla allá arriba. ¿Crees que existan otros mundos tan hermosos como este?"
                ),
                DialogueBeat(
                    speaker = Speaker.SPIDER_MAN,
                    text = "En todos los universos posibles hay telarañas, rascacielos y destinos... pero el único que realmente importa es este, porque aquí estás tú, Isabella.",
                    choices = listOf(
                        StoryChoice(
                            id = "ch5_forever",
                            text = "Sellar la historia canónica con un abrazo bajo los faroles",
                            traitTag = "Amor Canónico",
                            bondDelta = 15,
                            courageDelta = 8,
                            tendernessDelta = 15,
                            witDelta = 6,
                            responseSpeaker = Speaker.ISABELLA,
                            responseText = "Isabella entrelaza sus dedos con los tuyos: «Nuestro evento canónico apenas comienza.»"
                        )
                    )
                )
            ),
            completionMemoryId = "mem_eternal_letter"
        )
    )

    fun memoryById(id: String): CanonicalMemory? = memories.firstOrNull { it.id == id }

    fun chapterByNumber(number: Int): ChapterSpec =
        chapters.firstOrNull { it.number == number } ?: chapters.first()

    fun epilogueDedication(
        bondScore: Int,
        courage: Int,
        tenderness: Int,
        wit: Int
    ): Pair<String, String> {
        val dominant = when {
            tenderness >= courage && tenderness >= wit ->
                "Vínculo de Ternura Infinita"
            courage >= tenderness && courage >= wit ->
                "Vínculo de Valentía Inquebrantable"
            else ->
                "Vínculo de Complicidad Única"
        }

        val letter = when {
            bondScore >= 90 ->
                "Querida Isabella:\n\nCada decisión que tomaste iluminó el multiverso entero. Alcanzaste la máxima sintonía canónica ($bondScore%): una historia donde el amor vence cualquier vértigo, las noches de lluvia se transforman en promesas neón y hasta las estrellas llevan tu nombre.\n\nNo importa si juegas desde tu celular o desde la computadora: este universo siempre guardará nuestra historia.\n\nCon todo mi corazón,\nTu amigable vecino Spider-Man."
            else ->
                "Querida Isabella:\n\nGracias por balancearte conmigo a través de Queens, el Puente de Brooklyn y el Observatorio Estelar. Alcanzaste un $bondScore% de Vínculo Canónico ($dominant).\n\nCada rosa recogida, cada luz encendida y cada estrella tejida quedó grabada en nuestra crónica. Siempre puedes volver a jugar cualquier capítulo para descubrir nuevos recuerdos y cartas secretas.\n\nPara siempre tuyo,\nTu Spider-Man."
        }
        return dominant to letter
    }
}
