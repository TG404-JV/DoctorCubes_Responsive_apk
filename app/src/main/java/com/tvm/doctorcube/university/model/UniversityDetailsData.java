package com.tvm.doctorcube.university.model;

import com.tvm.doctorcube.R;

import java.util.HashMap;
import java.util.Map;

public class UniversityDetailsData {

    // Custom class to hold detailed university data
    public static class UniversityDetail {
        private int imageResourceId; // University image (default: flag_china)
        private String location; // City, Country
        private String description; // Overview text
        private String established; // Year established
        private String ranking; // Ranking info
        private String address; // Full address
        private String phone; // Contact phone
        private String email; // Contact email
        private String admissionRequirements; // Added admission requirements

        public UniversityDetail(int imageResourceId, String location, String description, String established,
                                String ranking, String address, String phone, String email, String admissionRequirements) {
            this.imageResourceId = imageResourceId;
            this.location = location;
            this.description = description;
            this.established = established;
            this.ranking = ranking;
            this.address = address;
            this.phone = phone;
            this.email = email;
            this.admissionRequirements = admissionRequirements; // Initialize
        }

        // Getters
        public int getImageResourceId() {
            return imageResourceId;
        }

        public String getLocation() {
            return location;
        }

        public String getDescription() {
            return description;
        }

        public String getEstablished() {
            return established;
        }

        public String getRanking() {
            return ranking;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getAdmissionRequirements() {
            return admissionRequirements;
        }
    }

    // HashMap to store university details
    private static final Map<String, UniversityDetail> universityDetailsMap = new HashMap<>();

    static {


        // russia
        universityDetailsMap.put("Kemerovo State University", new UniversityDetail(
                R.drawable.img_kemerovo_state_medical_university,
                "Kemerovo, Russia",
                "Kemerovo State University (KemSU) is a leading institution in Siberia, offering a wide range of programs from humanities to sciences. Established in 1974, KemSU is known for its strong research focus and academic excellence. The university provides students with a comprehensive education, fostering critical thinking and innovation.\n\n" +
                        "KemSU's campus is equipped with modern facilities, including well-equipped laboratories, libraries, and research centers. Students benefit from experienced faculty and opportunities for research and practical training. The university also promotes cultural and extracurricular activities, enriching the student experience.\n\n" +
                        "Located in Kemerovo, the university offers a vibrant student life with access to various cultural and recreational activities. Graduates of KemSU are well-prepared for careers in diverse fields, contributing to regional and national development.",
                "1974",
                "Leading university in Siberia",
                "Krasnaya St, 6, Kemerovo, Russia",
                "+7 (3842) 58-70-00",
                "rector@kemsu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Kemerovo State Medical University", new UniversityDetail(
                R.drawable.img_orelstateuniversity,
                "Kemerovo, Russia",
                "Nestled in the heart of Siberia, *Kemerovo State Medical University* (KSMU) is a gem for aspiring doctors. Founded in 1955, this public institution has been shaping skilled medical professionals for decades. Known for its warm, supportive community and rigorous academic standards, KSMU offers a *General Medicine (MBBS) program* that blends cutting-edge science with real-world clinical practice. The six-year journey here is accredited by Russia’s *Ministry of Science and Higher Education* and recognized globally, making it a fantastic choice for students dreaming of a medical career.\n\n" +
                        "At KSMU, you’ll dive into a curriculum that balances *in-depth theoretical learning* with *hands-on training in top Kemerovo hospitals*. Imagine working alongside experienced doctors, mastering diagnostics, and treating patients in a vibrant clinical setting. The university boasts *modern simulation labs, advanced research facilities, and a faculty of passionate educators* who guide you every step of the way. It’s not just about books here—KSMU prepares you to save lives.\n\n" +
                        "Life in Kemerovo is cozy yet lively, with affordable living and a tight-knit student community. The university offers *comfortable dormitories, well-stocked libraries, and even sports facilities* to keep you balanced. Whether you’re exploring Siberia’s stunning landscapes or bonding with classmates over a warm meal, KSMU feels like a home away from home.\n\n" +
                        "Graduates from KSMU step into the world with confidence, landing roles in *hospitals, research centers, and healthcare organizations* across Russia and beyond. With its *A-grade reputation* and focus on practical skills, Kemerovo State Medical University is a launchpad for your medical dreams.",
                "1955",
                "A-grade medical institution in Russia",
                "Voroshilova St, 22A, Kemerovo, Russia",
                "+7 (3842) 39-65-00",
                "ksmu@kemgmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Orel State University", new UniversityDetail(
                R.drawable.img_orelstateuniversity,
                "Orel, Russia",
                "Orel State University named after I.S. Turgenev is a comprehensive university offering a wide array of programs. Established in 1931, it has grown into a significant educational and research center in the Central Federal District of Russia. The university is known for its strong academic traditions and commitment to innovation.\n\n" +
                        "OSU provides students with a well-rounded education, combining theoretical knowledge with practical skills. The university boasts modern facilities, including laboratories, libraries, and sports complexes. Students benefit from the expertise of experienced faculty and have opportunities for research and internships.\n\n" +
                        "The university's location in Orel offers a rich cultural and historical environment. Students can enjoy a vibrant student life with access to various cultural events and recreational activities. Graduates of OSU are well-prepared for diverse career paths.",
                "1931",
                "Significant educational and research center",
                "Komsomolskaya St, 95, Orel, Russia",
                "+7 (4862) 41-66-66",
                "rector@oreluniver.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Omsk State Medical University", new UniversityDetail(
                R.drawable.img_omsk_medicaluniver,
                "Omsk, Russia",
                "Omsk State Medical University is a leading medical institution in Siberia, renowned for its high-quality medical education and research. Founded in 1920, it has a long history of training skilled medical professionals. The university is committed to providing students with a comprehensive medical education.\n\n" +
                        "OSMU offers a range of medical programs, including General Medicine, Dentistry, and Pharmacy. Students benefit from modern facilities, including simulation centers and clinical hospitals. The university's faculty comprises experienced doctors and researchers, providing students with valuable practical experience.\n\n" +
                        "Located in Omsk, the university offers a vibrant student life with access to various cultural and recreational activities. Graduates of OSMU are highly regarded and find employment in hospitals and healthcare organizations across Russia and beyond.",
                "1920",
                "Leading medical institution in Siberia",
                "Lenina St, 12, Omsk, Russia",
                "+7 (3812) 23-02-92",
                "rector@omsk-osma.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Kabardino-Balkarian State University", new UniversityDetail(
                R.drawable.img_kabardino_balkarianstateuniversity,
                "Nalchik, Russia",
                "Kabardino-Balkarian State University named after H.M. Berbekov is a multidisciplinary university offering a wide range of academic programs. Established in 1957, it has become a major educational and research center in the North Caucasus region. The university is known for its diverse academic offerings and commitment to quality education.\n\n" +
                        "KBSU provides students with a comprehensive education, combining theoretical knowledge with practical skills. The university boasts modern facilities, including laboratories, libraries, and research centers. Students benefit from experienced faculty and have opportunities for research and internships.\n\n" +
                        "Located in Nalchik, the university offers a unique cultural environment with access to the natural beauty of the Caucasus Mountains. Students can enjoy a vibrant student life with access to various cultural and recreational activities. Graduates of KBSU are well-prepared for diverse career paths.",
                "1957",
                "Major educational and research center",
                "Chernyshevsky St, 173, Nalchik, Russia",
                "+7 (8662) 42-37-88",
                "rector@kbsu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Maikop State Technological University", new UniversityDetail(
                R.drawable.img_chechen_stateuniversity,
                "Maikop, Russia",
                "Maikop State Technological University (MSTU) is a leading technological university in the Republic of Adygea. Established in 1993, it has grown into a significant educational and research center. The university is known for its strong focus on technology, engineering, and applied sciences.\n\n" +
                        "MSTU provides students with a high-quality education, combining theoretical knowledge with practical skills. The university boasts modern facilities, including laboratories, workshops, and research centers. Students benefit from experienced faculty and have opportunities for internships and industrial collaborations.\n\n" +
                        "Located in Maikop, the university offers a unique blend of technological education and cultural experiences. Students can enjoy a vibrant student life with access to various cultural and recreational activities. Graduates of MSTU are well-prepared for careers in technology, engineering, and related fields.",
                "1993",
                "Leading technological university",
                "Pervomayskaya St, 144, Maikop, Russia",
                "+7 (8772) 52-30-22",
                "rector@mkgtu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Yaroslavl State University", new UniversityDetail(
                R.drawable.img_yaroslaval_statemedicaluniversity,
                "Yaroslavl, Russia",
                "Yaroslavl State University, named after P.G. Demidov, is one of the oldest universities in Russia, with its history dating back to 1803. It offers a wide range of programs in humanities, natural sciences, and economics. The university is known for its strong academic traditions and commitment to providing students with a comprehensive education.\n\n" +
                        "The university has modern facilities, including well-equipped laboratories, libraries, and computer centers. Students benefit from experienced faculty and have opportunities for research and practical training. Yaroslavl State University also promotes cultural and extracurricular activities, contributing to a vibrant student life.\n\n" +
                        "Located in Yaroslavl, a historic city on the Volga River, the university provides a rich cultural environment. Graduates of Yaroslavl State University are well-prepared for careers in various fields, both in Russia and internationally.",
                "1918 (successor to Demidov Lyceum, founded in 1803)",
                "One of the oldest universities in Russia",
                "Sovetskaya St, 14, Yaroslavl, Russia",
                "+7 (4852) 72-87-02",
                "rector@uniyar.ac.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Chechen State University", new UniversityDetail(
                R.drawable.img_chechen_stateuniversity,
                "Grozny, Russia",
                "Chechen State University is a major institution of higher education in the Chechen Republic. Founded in 1938, it offers a wide range of programs in various fields, including humanities, natural sciences, and medicine. The university plays a crucial role in the educational and cultural development of the region.\n\n" +
                        "The university provides students with a comprehensive education, combining theoretical knowledge with practical skills. It has modern facilities, including laboratories, libraries, and computer centers. Chechen State University is committed to fostering a supportive and inclusive learning environment.\n\n" +
                        "Located in Grozny, the capital of the Chechen Republic, the university offers a unique cultural experience. Graduates of Chechen State University contribute to the social and economic development of the region and beyond.",
                "1938",
                "Major institution in the Chechen Republic",
                "Sheripova St, 32, Grozny, Russia",
                "+7 (8712) 29-41-46",
                "rector@chesu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Krasnoyarsk State Medical University", new UniversityDetail(
                R.drawable.img_krasnoyarsk_statemedical_university,
                "Krasnoyarsk, Russia",
                "Krasnoyarsk State Medical University named after Professor V.F. Voyno-Yasenetsky is a leading medical university in Siberia. Established in 1942, it has a long history of training highly qualified medical professionals. The university is known for its strong emphasis on clinical practice and research.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, and Dentistry. Students benefit from modern facilities, including simulation centers and clinical hospitals. The faculty comprises experienced doctors and researchers who provide students with valuable practical experience.\n\n" +
                        "Located in Krasnoyarsk, a major city in Siberia, the university offers a vibrant student life with access to various cultural and recreational activities. Graduates of Krasnoyarsk State Medical University are highly regarded and find employment in healthcare institutions across Russia and internationally.",
                "1942",
                "Leading medical university in Siberia",
                "Partizana Zheleznyaka St, 1, Krasnoyarsk, Russia",
                "+7 (391) 220-13-24",
                "rector@krasgmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Ulyanovsk State University", new UniversityDetail(
                R.drawable.img_ulyanovsk_statemedicaluniverrsity,
                "Ulyanovsk, Russia",
                "Ulyanovsk State University is a comprehensive university offering a wide range of programs in various fields, including humanities, natural sciences, and engineering. Founded in 1988, it has quickly grown into a major educational and research center in the Volga region.\n\n" +
                        "The university provides students with a modern education, combining theoretical knowledge with practical skills. It has well-equipped facilities, including laboratories, libraries, and technology parks. Ulyanovsk State University is committed to innovation and research, fostering a dynamic learning environment.\n\n" +
                        "Located in Ulyanovsk, a city with a rich historical and cultural heritage, the university offers a vibrant student life. Graduates of Ulyanovsk State University are well-prepared for careers in diverse fields, contributing to the development of the region and the country.",
                "1988",
                "Major educational and research center in the Volga region",
                "Nakhimova St, 12, Ulyanovsk, Russia",
                "+7 (8422) 37-01-66",
                "rector@ulsu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Ural State Medical University", new UniversityDetail(
                R.drawable.img_ural_statemedical_university,
                "Yekaterinburg, Russia",
                "Ural State Medical University is one of the oldest medical schools in Russia, with its history dating back to 1930. It is a leading center for medical education, research, and clinical practice. The university is known for its strong academic traditions and commitment to training highly qualified medical professionals.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, and Dentistry. Students have access to modern facilities, including simulation centers and university clinics. The faculty comprises experienced doctors and researchers who provide students with comprehensive theoretical and practical training.\n\n" +
                        "Located in Yekaterinburg, a major city in the Ural region, the university offers a vibrant student life with numerous cultural and recreational opportunities. Graduates of Ural State Medical University are highly valued and work in healthcare institutions across Russia and internationally.",
                "1930",
                "One of the oldest medical schools in Russia",
                "Repina St, 3, Yekaterinburg, Russia",
                "+7 (343) 214-70-37",
                "rector@usmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Altai State Medical University", new UniversityDetail(
                R.drawable.img_altai_statemedicaluniversity,
                "Barnaul, Russia",
                "Altai State Medical University is a leading medical university in the Altai Krai region. Established in 1954, it has a strong reputation for providing high-quality medical education and training. The university is committed to preparing students for successful careers in healthcare.\n\n" +
                        "The university offers various medical programs, including General Medicine, Pediatrics, and Pharmacy. Students benefit from modern facilities, including laboratories, simulation centers, and clinical bases. The faculty comprises experienced medical professionals and researchers dedicated to student success.\n\n" +
                        "Located in Barnaul, the capital of the Altai Krai, the university offers a supportive learning environment with access to the region's natural beauty and cultural attractions. Graduates of Altai State Medical University are well-prepared to meet the healthcare needs of the community and beyond.",
                "1954",
                "Leading medical university in the Altai Krai",
                "Lenina St, 40, Barnaul, Russia",
                "+7 (3852) 56-68-80",
                "rector@agmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Syktyvkar State University", new UniversityDetail(
                R.drawable.img_syktyvkar_statemedicaluniversity,
                "Syktyvkar, Russia",
                "Syktyvkar State University named after Pitirim Sorokin is a major institution of higher education in the Komi Republic. Founded in 1972, it offers a wide range of programs in various fields, including humanities, natural sciences, and engineering. The university plays a key role in the educational, research, and cultural life of the region.\n\n" +
                        "The university provides students with a comprehensive education, combining theoretical knowledge with practical skills. It has modern facilities, including laboratories, libraries, and computer centers. Syktyvkar State University is committed to fostering a supportive and inclusive learning environment.\n\n" +
                        "Located in Syktyvkar, the capital of the Komi Republic, the university offers a unique cultural experience in a region known for its rich natural resources and traditions. Graduates of Syktyvkar State University contribute to the social and economic development of the region and beyond.",
                "1972",
                "Major institution in the Komi Republic",
                "Oktyabrsky Ave, 55, Syktyvkar, Russia",
                "+7 (8212) 21-55-00",
                "rector@syktsu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Northwestern State Medical University", new UniversityDetail(
                R.drawable.img_northwestern_statemedicaluniversity,
                "Saint Petersburg, Russia",
                "Northwestern State Medical University named after I.I. Mechnikov is one of the oldest and most prestigious medical universities in Russia. Established in 2011 through the merger of two older institutions, it continues a long tradition of excellence in medical education, research, and clinical practice.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, and Dentistry. Students benefit from state-of-the-art facilities, including advanced simulation centers and university hospitals. The faculty comprises renowned doctors and researchers who provide students with exceptional training.\n\n" +
                        "Located in Saint Petersburg, a major center of culture and science, the university offers a rich and vibrant environment for students. Graduates of Northwestern State Medical University are highly respected and pursue successful careers in healthcare in Russia and worldwide.",
                "2011 (merger of older institutions)",
                "One of the oldest and most prestigious medical universities in Russia",
                "Kirochnaya St, 41, Saint Petersburg, Russia",
                "+7 (812) 543-11-11",
                "rector@szgmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Siberian State Medical University", new UniversityDetail(
                R.drawable.img_siberrian_statemedicaluniversity,
                "Tomsk, Russia",
                "Siberian State Medical University is one of the oldest medical schools in Siberia, founded in 1878. It is a leading center for medical education, research, and clinical practice. The university is known for its strong emphasis on clinical training and preparing highly qualified medical professionals.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, and Pharmacy. Students have access to modern facilities, including simulation centers and university hospitals. The faculty comprises experienced doctors and researchers who provide students with comprehensive theoretical and practical training.\n\n" +
                        "Located in Tomsk, one of the oldest cities in Siberia, the university offers a rich cultural and academic environment. Graduates of Siberian State Medical University are highly valued and work in healthcare institutions across Russia and internationally.",
                "1878",
                "One of the oldest medical schools in Siberia",
                "Moskovsky Trakt, 2, Tomsk, Russia",
                "+7 (3822) 90-11-01",
                "rector@ssmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Kazan State Medical University", new UniversityDetail(
                R.drawable.img_kazan_statemedical_university,
                "Kazan, Russia",
                "Kazan State Medical University is a leading medical university in Russia, with a history dating back to 1814. It is a renowned center for medical education, research, and clinical practice. The university is committed to providing students with a high-quality medical education and preparing them for successful careers in healthcare.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, and Dentistry. Students have access to modern facilities, including simulation centers and university clinics. The faculty comprises experienced doctors and researchers who are dedicated to teaching and mentoring students.\n\n" +
                        "Located in Kazan, a vibrant city with a rich cultural and historical heritage, the university offers a dynamic and supportive learning environment. Graduates of Kazan State Medical University are highly respected and work in healthcare institutions throughout Russia and worldwide.",
                "1814",
                "Leading medical university in Russia",
                "Butlerova St, 49, Kazan, Russia",
                "+7 (843) 236-00-92",
                "rector@kazangmu.ru",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Russian language proficiency (TORFL) or preparatory language course available."
        ));

        universityDetailsMap.put("Tula State University", new UniversityDetail(
                R.drawable.img_tula_stateuniversity,
                "Tula, Russia",
                "Tula State University is a major institution of higher education in the Tula region. Founded in 1930, it offers a wide range of programs in various fields, including engineering, natural sciences, humanities, and economics. The university is committed to providing students with a comprehensive education and preparing them for successful careers.\n\n" +
                        "The university has modern facilities, including well-equipped laboratories, libraries, and computer centers. Students benefit from experienced faculty and have opportunities for research and practical training. Tula State University also promotes cultural and extracurricular activities, contributing to a vibrant student life.\n\n" +
                        "Located in Tula, a city with a rich industrial and cultural heritage, the university provides a dynamic and supportive learning environment. Graduates of Tula State University are well-prepared for careers in various fields, contributing to the development of the region and the country.",
                "1930",
                "Major institution in the Tula region",
                "Lenina Ave, 92, Tula, Russia",
                "+7 (4872) 33-25-70",
                "rector@tsu.tula.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Khanty-Mansiysk State University", new UniversityDetail(
                R.drawable.img_kanti_mansiykuniversity,
                "Khanty-Mansiysk, Russia",
                "Khanty-Mansiysk State University is a leading institution of higher education in the Khanty-Mansi Autonomous Okrug – Yugra. Founded in 1993, it offers a wide range of programs in various fields, including humanities, natural sciences, and technology. The university is committed to providing students with a high-quality education and fostering research and innovation.\n\n" +
                        "The university has modern facilities, including well-equipped laboratories, libraries, and computer centers. Students benefit from experienced faculty and have opportunities for research and practical training. Khanty-Mansiysk State University also promotes cultural and extracurricular activities, contributing to a vibrant student life.\n\n" +
                        "Located in Khanty-Mansiysk, the capital of the region, the university offers a unique cultural and natural environment. Graduates of Khanty-Mansiysk State University are well-prepared for careers in various fields, contributing to the development of the region and beyond.",
                "1993",
                "Leading institution in the Khanty-Mansi Autonomous Okrug – Yugra",
                "Mira St, 15, Khanty-Mansiysk, Russia",
                "+7 (3467) 35-71-00",
                "rector@hmgu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Ingush State University", new UniversityDetail(
                R.drawable.img_ingush_statemedicaluniversity,
                "Magas, Russia",
                "Ingush State University is a major institution of higher education in the Republic of Ingushetia. Founded in 1994, it offers a range of programs in various fields, including humanities, natural sciences, and economics. The university plays an important role in the educational and cultural development of the region.\n\n" +
                        "The university provides students with a comprehensive education, combining theoretical knowledge with practical skills. It has modern facilities, including laboratories, libraries, and computer centers. Ingush State University is committed to fostering a supportive and inclusive learning environment.\n\n" +
                        "Located in Magas, the capital of the Republic of Ingushetia, the university offers a unique cultural experience. Graduates of Ingush State University contribute to the social and economic development of the region.",
                "1994",
                "Major institution in the Republic of Ingushetia",
                "Gagarina St, 21, Magas, Russia",
                "+7 (8734) 55-14-40",
                "rector@ingsu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Petrozavodsk State University", new UniversityDetail(
                R.drawable.img_petrozavodsk_statemedicaluniversity,
                "Petrozavodsk, Russia",
                "Petrozavodsk State University is a leading university in the Republic of Karelia. Founded in 1940, it offers a wide range of programs in various fields, including humanities, natural sciences, engineering, and medicine. The university is known for its strong academic traditions and commitment to providing students with a high-quality education.\n\n" +
                        "The university has modern facilities, including well-equipped laboratories, libraries, and computer centers. Students benefit from experienced faculty and have opportunities for research and practical training. Petrozavodsk State University also promotes cultural and extracurricular activities, contributing to a vibrant student life.\n\n" +
                        "Located in Petrozavodsk, the capital of the Republic of Karelia, the university offers a unique cultural and natural environment. Graduates of Petrozavodsk State University are well-prepared for careers in various fields, contributing to the development of the region and beyond.",
                "1940",
                "Leading university in the Republic of Karelia",
                "Lenina Ave, 33, Petrozavodsk, Russia",
                "+7 (8142) 71-10-01",
                "rector@petrsu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        universityDetailsMap.put("Rostov State University", new UniversityDetail(
                R.drawable.img_rostov_statemedicaluniversity,
                "Rostov-on-Don, Russia",
                "Rostov State University, now known as Southern Federal University, is one of the largest and leading universities in Southern Russia. Founded in 1915, it offers a wide range of programs in various fields, including humanities, natural sciences, engineering, and medicine. The university is a major center for education, research, and innovation.\n\n" +
                        "The university has modern facilities, including well-equipped laboratories, libraries, and research centers. Students benefit from experienced faculty and have opportunities for research and practical training. Southern Federal University is committed to providing a high-quality education and fostering a dynamic learning environment.\n\n" +
                        "Located in Rostov-on-Don, a major city in Southern Russia, the university offers a vibrant student life with numerous cultural and recreational opportunities. Graduates of Southern Federal University are highly valued and pursue successful careers in various fields, contributing to the development of the region and the country.",
                "1915",
                "One of the largest and leading universities in Southern Russia",
                "Bolshaya Sadovaya St, 105/42, Rostov-on-Don, Russia",
                "+7 (863) 218-19-99",
                "rector@sfedu.ru",
                "High school diploma. Entrance exams vary by program. Russian language proficiency or completion of a preparatory course."
        ));

        // Uzbekistan Universities

        universityDetailsMap.put("Tashkent State Medical University", new UniversityDetail(
                R.drawable.img_tashkent_medicalacademy,
                "Tashkent, Uzbekistan",
                "Tashkent State Medical University is one of the oldest and most prestigious medical universities in Uzbekistan. Established in 1920, it has a long history of training highly qualified medical professionals. The university is known for its strong academic programs, clinical training, and research activities.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, Dentistry, and Pharmacy. Students have access to modern facilities, including simulation centers, university clinics, and research laboratories. The faculty comprises experienced doctors and researchers who are dedicated to providing students with a comprehensive medical education.\n\n" +
                        "Located in Tashkent, the capital of Uzbekistan, the university offers a vibrant cultural and academic environment. Graduates of Tashkent State Medical University are highly respected and work in healthcare institutions across Uzbekistan and internationally.",
                "1920",
                "One of the oldest and most prestigious medical universities in Uzbekistan",
                "Farabi St, 2, Tashkent, Uzbekistan",
                "+998 (71) 214-62-78",
                "info@tsmu.uz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Uzbek or Russian language."
        ));

        universityDetailsMap.put("Zermed State Medical University", new UniversityDetail(
                R.drawable.img_zhejianguniversity,
                "Tashkent, Uzbekistan",
                "Unfortunately, I couldn't find specific details for a university named \"Zermed State Medical University\" in Uzbekistan. It's possible that the name is slightly different or that it's a newer institution with limited publicly available information. If you can provide an alternate name or more details, I may be able to find the information you need.\n\nHowever, I can provide you with information about other medical universities in Uzbekistan.",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A",
                "N/A"
        ));


        universityDetailsMap.put("Samarkand State Medical University", new UniversityDetail(
                R.drawable.img_samarkand_statemedicaluniversity,
                "Samarkand, Uzbekistan",
                "Samarkand State Medical University is one of the leading medical universities in Uzbekistan. Founded in 1930, it has a long history of training qualified medical professionals. The university is known for its strong clinical training, research activities, and contribution to the healthcare system of Uzbekistan.\n\n" +
                        "The university offers a wide range of medical programs, including General Medicine, Pediatrics, Dentistry, and Medical Prevention. Students have access to modern facilities, including simulation centers, university clinics, and research laboratories. The faculty comprises experienced doctors and researchers who are dedicated to providing students with a high-quality medical education.\n\n" +
                        "Located in Samarkand, a historic city with a rich cultural heritage, the university offers a unique and inspiring learning environment. Graduates of Samarkand State Medical University are highly respected and work in healthcare institutions across Uzbekistan and internationally.",
                "1930",
                "Leading medical university in Uzbekistan",
                "Amir Temur St, 18, Samarkand, Uzbekistan",
                "+998 (66) 233-63-92",
                "info@sammi.uz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Uzbek or Russian language."
        ));

        universityDetailsMap.put("Bukhara State Medical University", new UniversityDetail(
                R.drawable.img_bukhara_statemedicaluniversity,
                "Bukhara, Uzbekistan",
                "Bukhara State Medical Institute is a higher education institution in the city of Bukhara.\n\nIt trains medical specialists in the following areas:\n\nGeneral medicine;\nPediatrics;\nDentistry;\nPharmacy.\n\nBukhara State Medical Institute was established in 1990. In 2020, the institute was transformed into a university.",
                "1990",
                "Higher education institution",
                "Navoi str., 1, Bukhara, Uzbekistan",
                "+(998) 65 221-25-33",
                "info@bsmi.uz",
                ""
        ));

        universityDetailsMap.put("Tashkent Medical Academy Chirchiq Branch", new UniversityDetail(
                R.drawable.img_takshet_medicaluniversity_chirchiq_branch,
                "Chirchiq, Uzbekistan",
                "The Tashkent Medical Academy Chirchiq Branch is a branch of the Tashkent Medical Academy, one of the leading medical education institutions in Uzbekistan.  It provides medical education and training in the city of Chirchiq.\n\nThe branch likely offers programs similar to the main academy, focusing on training medical professionals for various healthcare roles.  As a branch of a well-established institution, it likely maintains a similar standard of education.\n\nInformation on specific programs, facilities, and admission requirements may be best obtained from the official Tashkent Medical Academy website or by contacting the branch directly.",
                "N/A",
                "Branch of Tashkent Medical Academy",
                "Chirchiq, Uzbekistan (Specific address may require further research)",
                "Contact information can be obtained through the main Tashkent Medical Academy.",
                "Information can be obtained through the main Tashkent Medical Academy.",
                "Admission requirements are likely similar to the main Tashkent Medical Academy: High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Uzbek or Russian language."
        ));

        // Georgia Universities

        universityDetailsMap.put("Geomedi Medical University", new UniversityDetail(
                R.drawable.img_geomedi_medicaluniversity,
                "Tbilisi, Georgia",
                "Geomedi Medical University is a private medical university located in Tbilisi, Georgia. It was founded in 2003 and has quickly grown into a prominent medical education institution in the country. The university is known for its focus on providing students with a high-quality medical education and preparing them for successful careers in healthcare.\n\n" +
                        "Geomedi Medical University offers a range of undergraduate and graduate programs in medicine, including General Medicine, Dentistry, and Pharmacy. The university provides students with modern facilities, including well-equipped laboratories, clinics, and simulation centers. The faculty comprises experienced professors and healthcare professionals who are dedicated to teaching and mentoring students.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, Geomedi Medical University offers a vibrant and culturally rich environment for students. The university attracts both local and international students and is committed to fostering a diverse and inclusive learning community.",
                "2003",
                "Private medical university in Tbilisi",
                "David Agmashenebeli Alley 144, Tbilisi, Georgia",
                "+995 32 240 04 12",
                "info@geomedi.edu.ge",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. English language proficiency."
        ));

        universityDetailsMap.put("Alte University", new UniversityDetail(
                R.drawable.img_alte_university,
                "Tbilisi, Georgia",
                "Alte University is a private higher education institution located in Tbilisi, Georgia. It offers a variety of programs across different fields of study, including business, law, and medicine. The university is committed to providing students with a modern and practical education that prepares them for the demands of the global market.\n\n" +
                        "Alte University has modern facilities, including well-equipped classrooms, libraries, and computer labs. The university emphasizes interactive teaching methods and provides students with opportunities for internships and practical experience. The faculty comprises experienced professors and industry professionals who are dedicated to student success.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, Alte University offers a dynamic and international learning environment. The university attracts students from diverse backgrounds and is committed to fostering a culture of innovation and entrepreneurship.",
                "2002",
                "Private higher education institution",
                "David Agmashenebeli Alley 144, Tbilisi, Georgia",
                "+995 32 244 66 77",
                "info@alte.edu.ge",
                "High school diploma. Entrance exam varies by program. English language proficiency."
        ));

        universityDetailsMap.put("Tbilisi State Medical University", new UniversityDetail(
                R.drawable.img_tbilisi_statemedicaluniversity,
                "Tbilisi, Georgia",
                "Tbilisi State Medical University is one of the oldest and most prestigious medical universities in Georgia. Founded in 1918, it has a long history of training highly qualified medical professionals. The university is a leading center for medical education, research, and clinical practice in the region.\n\n" +
                        "Tbilisi State Medical University offers a wide range of undergraduate and graduate programs in medicine, including General Medicine, Dentistry, Pharmacy, and Public Health. The university has extensive clinical facilities, including university hospitals and clinics, where students gain practical experience. The faculty comprises renowned professors and researchers who are dedicated to advancing medical knowledge and educating future healthcare leaders.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, Tbilisi State Medical University attracts students from around the world and offers a vibrant and international learning environment.",
                "1918",
                "One of the oldest and most prestigious medical universities in Georgia",
                "Vazha Pshavela Ave. 33, Tbilisi, Georgia",
                "+995 32 254 24 51",
                "info@tsmu.edu",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. English language proficiency."
        ));

        universityDetailsMap.put("SEU Georgia", new UniversityDetail(
                R.drawable.img_seu_georgia,
                "Tbilisi, Georgia",
                "SEU Georgia is a private university located in Tbilisi, Georgia. It offers a variety of programs across different fields of study, including business, law, international relations, and medicine. The university is committed to providing students with a high-quality education that meets international standards.\n\n" +
                        "SEU Georgia has modern facilities, including well-equipped classrooms, libraries, and computer labs. The university emphasizes student-centered learning and provides students with opportunities for internships and practical experience. The faculty comprises experienced professors and industry professionals who are dedicated to student success.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, SEU Georgia offers a dynamic and international learning environment. The university attracts students from diverse backgrounds and is committed to fostering a culture of academic excellence and innovation.",
                "2001",
                "Private university in Tbilisi, Georgia",
                "Tsinamdzgvrishvili St. 9, Tbilisi, Georgia",
                "+995 32 290 00 00",
                "info@seu.edu.ge",
                "High school diploma. Entrance exam varies by program. English language proficiency."
        ));

        universityDetailsMap.put("University of Georgia Tbilisi", new UniversityDetail(
                R.drawable.img_universityofgeorgia_tbilisi,
                "Tbilisi, Georgia",
                "The University of Georgia (UG) is a private university located in Tbilisi, Georgia. Founded in 2005, it has quickly become one of the leading higher education institutions in the country. The university offers a wide range of programs across various fields of study, including business, law, medicine, and humanities.\n\n" +
                        "The University of Georgia has modern facilities, including well-equipped classrooms, libraries, and research centers. The university emphasizes student-centered learning and provides students with opportunities for internships, study abroad programs, and research projects. The faculty comprises experienced professors and industry professionals who are dedicated to providing students with a high-quality education.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, the University of Georgia offers a vibrant and international learning environment. The university attracts students from diverse backgrounds and is committed to fostering a culture of academic excellence and innovation.",
                "2005",
                "Private university in Tbilisi, Georgia",
                "David Agmashenebeli Alley 140, Tbilisi, Georgia",
                "+995 32 255 05 00",
                "info@ug.edu.ge",
                "High school diploma. Entrance exam varies by program. English language proficiency."
        ));

        universityDetailsMap.put("Batumi Shota Rustaveli State University", new UniversityDetail(
                R.drawable.img_batumi_shotastateuniversity,
                "Batumi, Georgia",
                "Batumi Shota Rustaveli State University is a public university located in Batumi, Georgia. Established in 1945, it is one of the oldest and largest universities in the Adjara region. The university offers a variety of programs across different fields of study, including humanities, natural sciences, and medicine.\n\n" +
                        "Batumi Shota Rustaveli State University has modern facilities, including well-equipped classrooms, laboratories, and libraries. The university is committed to providing students with a high-quality education and preparing them for successful careers. The faculty comprises experienced professors and researchers who are dedicated to student success.\n\n" +
                        "Located in Batumi, a major city on the Black Sea coast, the university offers a unique cultural and natural environment. Batumi Shota Rustaveli State University attracts students from both Georgia and neighboring countries.",
                "1945",
                "Public university in Batumi, Georgia",
                "6010, Rustaveli St. 35, Batumi, Georgia",
                "+995 (422) 27-17-30",
                "info@bsu.edu.ge",
                "High school diploma. Entrance exam varies by program. Georgian language proficiency; English proficiency for some programs."
        ));

        universityDetailsMap.put("East European University", new UniversityDetail(
                R.drawable.img_easteuropean_university,
                "Tbilisi, Georgia",
                "East European University (EEU) is a private university located in Tbilisi, Georgia. Founded in 2012, it has quickly grown into a recognized higher education institution in the country. The university offers a variety of programs across different fields of study, including law, business, and humanities.\n\n" +
                        "East European University is committed to providing students with a modern and practical education that meets international standards. The university has modern facilities, including well-equipped classrooms, libraries, and computer labs. The faculty comprises experienced professors and industry professionals who are dedicated to student success.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, East European University offers a dynamic and international learning environment. The university attracts students from diverse backgrounds and is committed to fostering a culture of academic excellence and innovation.",
                "2012",
                "Private university in Tbilisi, Georgia",
                "Tbilisi, Georgia, st. Tsinamdzghvrishvili 136",
                "+995 599 15 80 14",
                "info@eeu.edu.ge",
                "High school diploma. Entrance exam varies by program. English language proficiency."
        ));

        universityDetailsMap.put("Georgian American University", new UniversityDetail(
                R.drawable.img_georgian_americanuniversity,
                "Tbilisi, Georgia",
                "The Georgian American University (GAU) is a private university located in Tbilisi, Georgia. Established in 2001, it offers a variety of undergraduate and graduate programs in English across different fields of study, including business, law, social sciences, and technology.\n\n" +
                        "GAU is committed to providing students with a high-quality American-style education. The university has modern facilities, including well-equipped classrooms, libraries, and computer labs. The faculty comprises experienced professors with international experience who are dedicated to student success.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, GAU offers a dynamic and international learning environment. The university attracts students from both Georgia and abroad and is known for its strong emphasis on critical thinking, research, and innovation.",
                "2001",
                "Private university in Tbilisi, Georgia",
                "Merab Aleksidze St #74, Tbilisi, Georgia",
                "+995 (32) 2 24 04 04",
                "info@gau.edu.ge",
                "High school diploma. Entrance exam varies by program. English language proficiency."
        ));

        universityDetailsMap.put("Ilia State University", new UniversityDetail(
                R.drawable.img_llia_stateuniversity,
                "Tbilisi, Georgia",
                "Ilia State University is a public research university located in Tbilisi, Georgia. Founded in 2006, it has quickly become one of the leading universities in the country. The university offers a wide range of programs across different fields of study, including natural sciences, humanities, business, and law.\n\n" +
                        "Ilia State University is committed to providing students with a high-quality education and fostering a culture of research and innovation. The university has modern facilities, including well-equipped laboratories, libraries, and research centers. The faculty comprises experienced professors and researchers who are dedicated to student success.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, Ilia State University offers a vibrant and dynamic learning environment. The university attracts students from diverse backgrounds and is known for its strong emphasis on academic freedom and critical thinking.",
                "2006",
                "Public research university in Tbilisi, Georgia",
                "3/5 Cholokashvili Ave, Tbilisi, Georgia",
                "+995 322 29 47 11",
                "info@iliauni.edu.ge",
                "High school diploma. Entrance exam varies by program. Georgian language proficiency; English proficiency for some programs."
        ));

        universityDetailsMap.put("New Vision University", new UniversityDetail(
                R.drawable.img_newvision_university,
                "Tbilisi, Georgia",
                "New Vision University is a private university located in Tbilisi, Georgia. Founded in 2013, it offers a range of programs in various fields, including medicine, law, business, and international relations. The university is committed to providing students with a modern and globally-oriented education.\n\n" +
                        "New Vision University has modern facilities, including well-equipped classrooms, libraries, and simulation centers for medical programs. The university emphasizes student-centered learning and provides students with opportunities for internships and practical experience. The faculty comprises experienced professors and industry professionals who are dedicated to student success.\n\n" +
                        "Located in Tbilisi, the capital of Georgia, New Vision University offers a dynamic and international learning environment. The university attracts students from diverse backgrounds and is committed to fostering a culture of innovation and academic excellence.",
                "2013",
                "Private university in Tbilisi, Georgia",
                "David Agmashenebeli Alley 144, Tbilisi, Georgia",
                "+995 32 242 17 17",
                "info@newvision.edu.ge",
                "High school diploma. Entrance exam varies by program. English language proficiency."
        ));

        // China Universities
        universityDetailsMap.put("Tianjin Medical University", new UniversityDetail(
                R.drawable.img_tianjin_medicaluniversity,
                "Tianjin, China",
                "Tianjin Medical University is a key medical university in China, known for its strong foundation in medical education and research. Founded in 1951, it has grown into a comprehensive medical center offering a wide range of programs.\n\nThe university provides programs in basic medical sciences, clinical medicine, public health, and nursing. It has several affiliated hospitals that provide students with extensive clinical experience. Tianjin Medical University is dedicated to advancing medical research and technology.\n\nLocated in Tianjin, a major port city in northern China, the university attracts students from across the country and internationally.",
                "1951",
                "Key medical university in China",
                "22 Qixiangtai Rd, Heping District, Tianjin, China",
                "+86 22 8333 6988",
                "iso@tmu.edu.cn",
                "High school diploma with strong grades in Biology, Chemistry, and Physics. Entrance exam required. Chinese language proficiency (HSK) or English language proficiency (for English-taught programs)."
        ));

        universityDetailsMap.put("Zhejiang University", new UniversityDetail(
                R.drawable.img_zhejianguniversity,
                "Hangzhou, China",
                "Zhejiang University is one of China's top universities, with a long history and strong reputation in various disciplines. Founded in 1897, it is a comprehensive research university with programs in sciences, engineering, agriculture, medicine, humanities, and management.\n\nThe university is organized into many schools and departments, hosting a large number of international students. It has strong research capabilities and is known for its beautiful campus in the city of Hangzhou.\n\nZhejiang University is consistently ranked among the best universities in China and Asia.",
                "1897",
                "One of China's top universities",
                "38 Zheda Rd, Xihu District, Hangzhou, Zhejiang, China",
                "+86 571 8795 1111",
                "zju@zju.edu.cn",
                "High school diploma. Entrance exam required. Chinese language proficiency (HSK) or English language proficiency (for English-taught programs)."
        ));

        universityDetailsMap.put("Jilin University", new UniversityDetail(
                R.drawable.img_jilin_university,
                "Changchun, China",
                "Jilin University is a leading national university in China, located in the city of Changchun. Founded in 1946, it is a comprehensive university offering a wide range of disciplines, including sciences, engineering, humanities, law, and medicine.\n\nThe university has a large student population and a strong faculty. It is known for its research achievements in various fields. Jilin University also has several campuses spread across the city of Changchun.\n\nJilin University is one of the largest universities in China.",
                "1946",
                "Leading national university in China",
                "2699 Qianjin St, Chaoyang District, Changchun, Jilin, China",
                "+86 431 8516 6114",
                "interadmission@jlu.edu.cn",
                "High school diploma. Entrance exam required. Chinese language proficiency (HSK) or English language proficiency (for English-taught programs)."
        ));

        universityDetailsMap.put("Shihezi University", new UniversityDetail(
                R.drawable.img_shihezi_university,
                "Shihezi, China",
                "Shihezi University is a comprehensive university located in Shihezi, Xinjiang, China. Founded in 1949, it offers programs in various fields, including agriculture, medicine, engineering, economics, and management.\n\nThe university plays an important role in the development of Xinjiang and Central Asia. It has a diverse student population and is committed to providing quality education and conducting research in the region.\n\nShihezi University is one of the larger universities in Xinjiang.",
                "1949",
                "Comprehensive university in Xinjiang",
                "South 4th Rd, Shihezi, Xinjiang, China",
                "+86 993 205 8015",
                "interoffice@shzu.edu.cn",
                "High school diploma. Entrance exam required. Chinese language proficiency (HSK) or English language proficiency (for English-taught programs)."
        ));

        universityDetailsMap.put("Wuhan University", new UniversityDetail(
                R.drawable.img_wuhan_university,
                "Wuhan, China",
                "Wuhan University is a prestigious comprehensive university located in Wuhan, China. Founded in 1893, it has a long history and is recognized as one of the top universities in China. The university offers a wide range of academic programs, including humanities, social sciences, natural sciences, engineering, and medicine.\n\nWuhan University is known for its beautiful campus, often referred to as one of the most scenic universities in China. It has a large student body and attracts students from all over the world. The university is dedicated to academic excellence and research.\n\nWuhan University is highly ranked both nationally and internationally.",
                "1893",
                "Prestigious comprehensive university",
                "8 Luojia Hill Rd, Wuchang District, Wuhan, Hubei, China",
                "+86 27 6875 9257",
                "admission@whu.edu.cn",
                "High school diploma. Entrance exam required. Chinese language proficiency (HSK) or English language proficiency (for English-taught programs)."
        ));

        universityDetailsMap.put("Fudan University", new UniversityDetail(
                R.drawable.img_fudanuniversity,
                "Shanghai, China",
                "Fudan University is one of the oldest and most prestigious universities in China, located in Shanghai. Founded in 1905, it is a comprehensive research university with a strong reputation in humanities, social sciences, natural sciences, engineering, and medicine.\n\nThe university has multiple campuses in Shanghai and is known for its rigorous academic environment and strong research output. Fudan University is consistently ranked among the top universities in China and the world.\n\nFudan University is a highly selective university with a strong global presence.",
                "1905",
                "One of the oldest and most prestigious universities in China",
                "220 Handan Rd, Yangpu District, Shanghai, China",
                "+86 21 6564 2254",
                "admission@fudan.edu.cn",
                "High school diploma. Entrance exam required. Chinese language proficiency (HSK) or English language proficiency (for English-taught programs)."
        ));


        // Kazakhstan Universities
        universityDetailsMap.put("Kazakh Russian Medical University", new UniversityDetail(
                R.drawable.img_kazak_russianuniversity,
                "Almaty, Kazakhstan",
                "Kazakh Russian Medical University (KRMU) is a private medical university located in Almaty, Kazakhstan. It is known for its focus on providing quality medical education with a blend of Russian and Kazakh medical traditions. KRMU offers a range of programs in various fields of medicine.\n\nThe university strives to equip students with the necessary knowledge and skills to excel in their medical careers. It emphasizes clinical practice and modern teaching methodologies.\n\nKRMU is located in Almaty, the largest city in Kazakhstan, providing students with a vibrant and diverse environment.",
                "2003",
                "Private medical university",
                "Almaty, Kazakhstan (Specific address details may require further research)",
                "Contact information can be obtained through their official website.",
                "info@krmu.kz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("West Kazakhstan Marat Ospanov State Medical University", new UniversityDetail(
                R.drawable.img_westkazak_marat_ospanovstateuniversity,
                "Aktobe, Kazakhstan",
                "West Kazakhstan Marat Ospanov State Medical University is a public medical university located in Aktobe, Kazakhstan. It is one of the leading medical education institutions in western Kazakhstan.\n\nThe university offers programs in various fields of medicine and emphasizes the training of qualified medical professionals for the region. It is committed to providing students with a comprehensive medical education.\n\nAktobe is a major city in western Kazakhstan, and the university plays a significant role in the region's healthcare development.",
                "1957",
                "Public medical university",
                "M. Ospanov Street 9, Aktobe, Kazakhstan",
                "+7 (7132) 54-58-55",
                "info@zkgmu.kz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("South Kazakhstan State Medical Academy, Kazakhstan", new UniversityDetail(
                R.drawable.img_southkazak_medicalacademy,
                "Shymkent, Kazakhstan",
                "South Kazakhstan State Medical Academy is a public medical academy located in Shymkent, Kazakhstan. It is a prominent medical education institution in southern Kazakhstan.\n\nThe academy provides training in various medical specialties and focuses on equipping students with the skills and knowledge necessary for successful medical practice. It plays a key role in supplying the region with healthcare professionals.\n\nShymkent is a major city in southern Kazakhstan, and the academy is an important center for medical education in the area.",
                "1979",
                "Public medical academy",
                "Al-Farabi Square 1, Shymkent, Kazakhstan",
                "+7 (7252) 39-57-15",
                "info@skma.kz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("Semey State Medical University", new UniversityDetail(
                R.drawable.img_semey_statemedical_university,
                "Semey, Kazakhstan",
                "Semey State Medical University is one of the oldest and most respected medical universities in Kazakhstan. Located in Semey, it has a long history of training highly qualified medical professionals.\n\nThe university offers a wide range of medical programs and is known for its strong clinical training and research activities. It has made significant contributions to medical science and practice in Kazakhstan.\n\nSemey has historical significance in Kazakhstan, and the university is a key institution in the city.",
                "1954",
                "One of the oldest medical universities in Kazakhstan",
                "Abai Street 103, Semey, Kazakhstan",
                "+7 (7222) 56-26-31",
                "info@ssmu.kz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("Astana Medical University", new UniversityDetail(
                R.drawable.img_astana_medicaluniversity,
                "Astana, Kazakhstan",
                "Astana Medical University is a leading medical university located in Astana, the capital of Kazakhstan. It is a modern and dynamic university that provides high-quality medical education and training.\n\nThe university offers a variety of programs in medicine and is committed to preparing students for the challenges of contemporary healthcare. It utilizes modern teaching technologies and emphasizes clinical skills development.\n\nBeing located in the capital city, Astana Medical University has strong ties to national healthcare institutions and plays a vital role in the country's medical education system.",
                "1964",
                "Leading medical university",
                "Myrza Street 47, Astana, Kazakhstan",
                "+7 (7172) 53-94-38",
                "info@amu.kz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("Kokshetau State University", new UniversityDetail(
                R.drawable.img_kokshetau_stateuniversity,
                "Kokshetau, Kazakhstan",
                "Kokshetau State University named after Sh. Ualikhanov is a regional university located in Kokshetau, Kazakhstan. While it offers various programs, it also has a faculty of medicine that provides medical education.\n\nThe university serves the educational needs of the region and provides students with a range of academic programs. The medical faculty contributes to the training of healthcare professionals for the local community.\n\nKokshetau is a city in northern Kazakhstan, and the university is an important educational and cultural center for the area.",
                "1962",
                "Regional university",
                "Auezov Street 189, Kokshetau, Kazakhstan",
                "+7 (7162) 25-63-01",
                "info@kgu.kz",
                "High school diploma. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("Karaganda State Medical University", new UniversityDetail(
                R.drawable.img_karagandastate_medicaluniversity,
                "Karaganda, Kazakhstan",
                "Karaganda State Medical University is one of the oldest and largest medical universities in Kazakhstan. Located in Karaganda, it has a long-standing tradition of providing high-quality medical education.\n\nThe university offers a comprehensive range of medical programs and is known for its strong emphasis on clinical training and research. It has produced many prominent medical professionals who have contributed to the development of healthcare in Kazakhstan.\n\nKaraganda is a major industrial city in central Kazakhstan, and the university is a key center for medical education in the region.",
                "1950",
                "One of the oldest and largest medical universities in Kazakhstan",
                "Gogol Street 40, Karaganda, Kazakhstan",
                "+7 (7212) 50-06-30",
                "info@kgmu.kz",
                "High school diploma with strong grades in Biology and Chemistry. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("North Kazakhstan State University", new UniversityDetail(
                R.drawable.img_northkazakh_statemedicaluniversity,
                "Petropavl, Kazakhstan",
                "North Kazakhstan State University named after M. Kozybaev is a regional university located in Petropavl, Kazakhstan. It offers a variety of programs, including a faculty of medicine that provides medical education.\n\nThe university serves the educational needs of northern Kazakhstan and offers a range of academic programs. The medical faculty contributes to training healthcare professionals for the local community.\n\nPetropavl is a city in northern Kazakhstan, and the university is a significant educational and cultural center in the area.",
                "1937",
                "Regional university",
                "Pushkin Street 86, Petropavl, Kazakhstan",
                "+7 (7152) 46-37-14",
                "info@nkzu.kz",
                "High school diploma. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        universityDetailsMap.put("Al-Farabi Kazakh National University", new UniversityDetail(
                R.drawable.img_al_farabi_kazakhnationaluniversity,
                "Almaty, Kazakhstan",
                "Al-Farabi Kazakh National University is the leading classical university in Kazakhstan, located in Almaty. Founded in 1933, it is a comprehensive university with a wide range of faculties, including medicine.\n\nThe university is a major center for education, science, and culture in Kazakhstan. It is committed to providing students with a high-quality education and fostering research and innovation.\n\nAl-Farabi Kazakh National University is highly ranked nationally and internationally.",
                "1933",
                "Leading classical university in Kazakhstan",
                "Al-Farabi Ave 71, Almaty, Kazakhstan",
                "+7 (727) 377-33-33",
                "info@kaznu.kz",
                "High school diploma. Entrance exam required. Proficiency in Russian or Kazakh language."
        ));

        // Nepal Universities
        universityDetailsMap.put("Kathmandu University", new UniversityDetail(
                R.drawable.img_kathmandu_university,
                "Kathmandu, Nepal",
                "Kathmandu University is a private university located in Dhulikhel, near Kathmandu, Nepal. Established in 1991, it is known for its quality education in various fields, including medicine, engineering, management, and arts.\n\nThe university is committed to providing students with a holistic education and has a strong emphasis on research and innovation. It has collaborations with various international institutions.\n\nKathmandu University is one of the leading universities in Nepal, attracting students from across the country and abroad.",
                "1991",
                "Private university",
                "Dhulikhel, Kathmandu, Nepal",
                "+977 11 661399",
                "info@ku.edu.np",
                "High school diploma. Entrance exam required for most programs. English language proficiency."
        ));

        universityDetailsMap.put("Tribhuvan University", new UniversityDetail(
                R.drawable.img_tribhuvan_university,
                "Kathmandu, Nepal",
                "Tribhuvan University is the oldest and largest public university in Nepal. Established in 1959, it has a vast network of campuses and affiliated colleges across the country. The university offers a wide range of programs in various disciplines, including humanities, science, management, and medicine.\n\nTribhuvan University plays a crucial role in providing higher education opportunities to a large segment of the population. It has a diverse student body and a long history of academic excellence.\n\nThe university's central campus is located in Kathmandu, but it has constituent campuses throughout Nepal.",
                "1959",
                "Oldest and largest public university in Nepal",
                "Kirtipur, Kathmandu, Nepal",
                "+977 1 4331084",
                "info@tu.edu.np",
                "High school diploma. Entrance exam required for most programs. English or Nepali language proficiency."
        ));

        universityDetailsMap.put("Institute of Medicine, Nepal", new UniversityDetail(
                R.drawable.img_institue_of_medicine,
                "Kathmandu, Nepal",
                "The Institute of Medicine (IOM) is a constituent institute of Tribhuvan University. It is the leading medical education institution in Nepal, with a long history of training healthcare professionals.\n\nIOM offers undergraduate and postgraduate programs in various medical fields, including medicine, dentistry, nursing, and public health. It has several teaching hospitals that provide students with clinical experience.\n\nThe institute plays a vital role in Nepal's healthcare system by producing skilled medical personnel.",
                "1972",
                "Constituent institute of Tribhuvan University",
                "Maharajgunj, Kathmandu, Nepal",
                "+977 1 4411163",
                "info@iom.edu.np",
                "High school diploma with strong grades in Biology, Chemistry, and Physics. Entrance exam required. English language proficiency."
        ));

    }

    /**
     * Retrieves the university details based on the university name.
     *
     * @param universityName The name of the university.
     * @return The UniversityDetail object, or null if not found.
     */
    public static UniversityDetail getUniversityDetails(String universityName) {
        return universityDetailsMap.get(universityName);
    }
}

