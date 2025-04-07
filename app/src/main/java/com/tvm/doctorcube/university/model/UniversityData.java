package com.tvm.doctorcube.university.model;

import com.tvm.doctorcube.R;
import java.util.ArrayList;
import java.util.List;

public class UniversityData {
    public static List<University> getUniversities() {
        List<University> universities = new ArrayList<>();


// Russia Universities

        // Kemerovo State Medical University
        universities.add(new University(
                "ru011",
                "Kemerovo State Medical University",
                "Kemerovo",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B+", 
                "September",
                "English/Russian",
                "Public",
                R.drawable.img_kemerovo_state_medical_university,
                R.drawable.logo_kemerovo_state,
                R.drawable.flag_russia,
                "Medical",
                "Top 600", 
                "Available" 
        ));

        // Orel State University
        universities.add(new University(
                "ru012",
                "Orel State University",
                "Oryol",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B", 
                "September",
                "English/Russian",
                "Public",
                R.drawable.img_orelstateuniversity,
                R.drawable.logo_orelstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Good", 
                "Check website" 
        ));

        // Omsk State Medical University
        universities.add(new University(
                "ru013",
                "Omsk State Medical University",
                "Omsk",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "A-", 
                "September",
                "English/Russian",
                "Public",
                R.drawable.img_omsk_medicaluniver,
                R.drawable.logo_omskstatemedical,
                R.drawable.flag_russia,
                "Medical",
                "Top 800", 
                "Limited" 
        ));

        // Kabardino-Balkarian State University
        universities.add(new University(
                "ru014",
                "Kabardino-Balkarian State University",
                "Nalchik",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "C+", 
                "September",
                "English/Russian",
                "Public",
                R.drawable.img_kabardino_balkarianstateuniversity,
                R.drawable.logo_kabardinouniversity,
                R.drawable.flag_russia,
                "Medical",
                "Average", 
                "Not specified" 
        ));

        // Chechen State University
        universities.add(new University(
                "ru015",
                "Chechen State University",
                "Grozny",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_chechen_stateuniversity,
                R.drawable.logo_chechenuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Ranked", 
                "Available"
        ));

        // Krasnoyarsk State Medical University
        universities.add(new University(
                "ru016",
                "Krasnoyarsk State Medical University",
                "Krasnoyarsk",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "A-",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_krasnoyarsk_statemedical_university,
                R.drawable.logo_krasnoyarskstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Top 700", 
                "Yes"
        ));

        // Ulyanovsk State University
        universities.add(new University(
                "ru017",
                "Ulyanovsk State University",
                "Ulyanovsk",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "C+",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_ulyanovsk_statemedicaluniverrsity,
                R.drawable.logo_ulyanovskstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Below Average", 
                "No"
        ));

        // Ural State Medical University
        universities.add(new University(
                "ru018",
                "Ural State Medical University",
                "Yekaterinburg",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "A",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_ural_statemedical_university,
                R.drawable.logo_uralstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Top 500", 
                "Check details"
        ));

        // Altai State Medical University
        universities.add(new University(
                "ru019",
                "Altai State Medical University",
                "Barnaul",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B+",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_altai_statemedicaluniversity,
                R.drawable.logo_altaistateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Good", 
                "Available"
        ));

        // Syktyvkar State University
        universities.add(new University(
                "ru020",
                "Syktyvkar State University",
                "Syktyvkar",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "C",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_syktyvkar_statemedicaluniversity,
                R.drawable.logo_syktyvkarstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Average", 
                "Check website"
        ));

        // Northwestern State Medical University named after I.I. Mechnikov
        universities.add(new University(
                "ru021",
                "Northwestern State Medical University named after I.I. Mechnikov",
                "Saint Petersburg",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "A",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_northwestern_statemedicaluniversity,
                R.drawable.logo_northwestern_stateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Top 300", 
                "Yes, inquire"
        ));

        // Siberian State Medical University
        universities.add(new University(
                "ru022",
                "Siberian State Medical University",
                "Tomsk",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B+",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_siberrian_statemedicaluniversity,
                R.drawable.logo_siberianstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Well-Ranked", 
                "Limited"
        ));

        // Kazan State Medical University
        universities.add(new University(
                "ru023",
                "Kazan State Medical University",
                "Kazan",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "A",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_kazan_statemedical_university,
                R.drawable.logo_west_kazakshthan_marat_university,
                R.drawable.flag_russia,
                "Medical",
                "Top 400", 
                "Available"
        ));

        // Tula State University
        universities.add(new University(
                "ru024",
                "Tula State University",
                "Tula",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "C+",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_tula_stateuniversity,
                R.drawable.logo_tulastateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Average", 
                "Not specified"
        ));

        // Khanty-Mansiysk State University

        // Replace The Banner image with the correct image
        universities.add(new University(
                "ru025",
                "Khanty-Mansiysk State University",
                "Khanty-Mansiysk",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B-",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_ural_statemedical_university,
                R.drawable.logo_uralstateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Below Average", 
                "No"
        ));

        // Ingush State University
        universities.add(new University(
                "ru026",
                "Ingush State University",
                "Magas",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "C",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_ingush_statemedicaluniversity,
                R.drawable.logo_ingushuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Low", 
                "None"
        ));

        // Petrozavodsk State University
        universities.add(new University(
                "ru027",
                "Petrozavodsk State University",
                "Petrozavodsk",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "B",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_petrozavodsk_statemedicaluniversity,
                R.drawable.logo_petrozavodsk_stateuniversity,
                R.drawable.flag_russia,
                "Medical",
                "Good", 
                "Check details"
        ));

        // Rostov State Medical University
        universities.add(new University(
                "ru028",
                "Rostov State Medical University",
                "Rostov-on-Don",
                "Russia",
                "General Medicine (MBBS)",
                "MD (Equivalent to MBBS)",
                "6 years",
                "A-",
                "September",
                "Russian/English",
                "Public",
                R.drawable.img_rostov_statemedicaluniversity,
                R.drawable.logo_rostovstateuniversty,
                R.drawable.flag_russia,
                "Medical",
                "Top 550", 
                "Yes"
        ));



        // Uzbekistan Universities
        universities.add(new University(
                "uz001",
                "Tashkent State Medical University",
                "Tashkent",
                "Uzbekistan",
                "General Medicine (MBBS)",
                "MD",
                "7 years",
                "A", 
                "September",
                "Uzbek/Russian/English",
                "Public",
                R.drawable.img_tashkent_medicalacademy,
                R.drawable.logo_tashkentstate_university,
                R.drawable.flag_uzbekistan,
                "Medical",
                "Top 500", 
                "Available"
        ));

        universities.add(new University(
                "uz002",
                "Zermed State Medical University",
                "Tashkent",
                "Uzbekistan",
                "General Medicine (MBBS)",
                "MD",
                "7 years",
                "B+",
                "September",
                "Uzbek/Russian/English",
                "Private",
                R.drawable.img_tashkent_medicalacademy,
                R.drawable.logo_tashkentstate_university,
                R.drawable.flag_uzbekistan,
                "Medical",
                "Top 700",
                "Check Website"
        ));

        universities.add(new University(
                "uz003",
                "Samarkand State Medical University",
                "Samarkand",
                "Uzbekistan",
                "General Medicine (MBBS)",
                "MD",
                "7 years",
                "A-",
                "September",
                "Uzbek/Russian/English",
                "Public",
                R.drawable.img_samarkand_statemedicaluniversity,
                R.drawable.logo_samarkandstateuniversity,
                R.drawable.flag_uzbekistan,
                "Medical",
                "Top 600",
                "Available"
        ));

        universities.add(new University(
                "uz004",
                "Bukhara State Medical University",
                "Bukhara",
                "Uzbekistan",
                "General Medicine (MBBS)",
                "MD",
                "7 years",
                "B",
                "September",
                "Uzbek/Russian/English",
                "Public",
                R.drawable.img_bukhara_statemedicaluniversity,
                R.drawable.logo_bukhara_state_medical_university,
                R.drawable.flag_uzbekistan,
                "Medical",
                "Good",
                "Check Details"
        ));

        universities.add(new University(
                "uz005",
                "Tashkent Medical Academy Chirchiq Branch",
                "Chirchiq",
                "Uzbekistan",
                "General Medicine (MBBS)",
                "MD",
                "7 years",
                "C+",
                "September",
                "Uzbek/Russian/English",
                "Public",
                R.drawable.img_tashkent_medicalacademy,
                R.drawable.logo_tashkentstate_university,
                R.drawable.flag_uzbekistan,
                "Medical",
                "Average",
                "N/A"
        ));



        // Georgia Universities
        universities.add(new University(
                "ge001",
                "Geomedi Medical University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "B+", 
                "September",
                "English",
                "Private",
                R.drawable.img_geomedi_medicaluniversity,
                R.drawable.logo_geomediuniversity,
                R.drawable.flag_georgia,
                "Medical",
                "Good", 
                "Available" 
        ));

        universities.add(new University(
                "ge002",
                "Alte University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "B", 
                "September",
                "English",
                "Private",
                R.drawable.img_alte_university,
                R.drawable.logo_alteuniversity,
                R.drawable.flag_georgia,
                "Medical",
                "Average", 
                "Check Website" 
        ));

        universities.add(new University(
                "ge003",
                "Tbilisi State Medical University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A-", 
                "September",
                "English",
                "Public",
                R.drawable.img_tbilisi_statemedicaluniversity,
                R.drawable.logo_tbilisistateuniversity,
                R.drawable.flag_georgia,
                "Medical",
                "Very Good", 
                "Limited" 
        ));

        universities.add(new University(
                "ge004",
                "SEU Georgia",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "C+", 
                "September",
                "English",
                "Private",
                R.drawable.img_seu_georgia,
                R.drawable.logo_seu_goergia,
                R.drawable.flag_georgia,
                "Medical",
                "Fair", 
                "Not specified" 
        ));

        universities.add(new University(
                "ge005",
                "University of Georgia Tbilisi",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "B",
                "September",
                "English",
                "Private",
                R.drawable.img_tbilisi_statemedicaluniversity,
                R.drawable.logo_tbilisistateuniversity,
                R.drawable.flag_georgia,
                "Medical",
                "Good", 
                "Available"
        ));

        universities.add(new University(
                "ge006",
                "Batumi Shota Rustaveli State University",
                "Batumi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "B-", 
                "September",
                "English",
                "Public",
                R.drawable.img_batumi_shotastateuniversity,
                R.drawable.logo_batumishotauniversity,
                R.drawable.flag_georgia,
                "Medical",
                "Average", 
                "Check Website"
        ));

        universities.add(new University(
                "ge007",
                "East European University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "C+", 
                "September",
                "English",
                "Private",
                R.drawable.img_easteuropean_university,
                R.drawable.logo_east_europeanuni,
                R.drawable.flag_georgia,
                "Medical",
                "Fair", 
                "Not specified"
        ));

        universities.add(new University(
                "ge008",
                "Georgian American University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "B",
                "September",
                "English",
                "Private",
                R.drawable.img_georgian_americanuniversity,
                R.drawable.logo_georgianamericanuni,
                R.drawable.flag_georgia,
                "Medical",
                "Good", 
                "Available"
        ));

        universities.add(new University(
                "ge009",
                "Ilia State University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A-", 
                "September",
                "English",
                "Public",
                R.drawable.img_llia_stateuniversity,
                R.drawable.logo_ingushuniversity,
                R.drawable.flag_georgia,
                "Medical",
                "Very Good", 
                "Limited"
        ));

        universities.add(new University(
                "ge010",
                "New Vision University",
                "Tbilisi",
                "Georgia",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A", 
                "September",
                "English",
                "Private",
                R.drawable.img_newvision_university,
                R.drawable.logo_newvision_university,
                R.drawable.flag_georgia,
                "Medical",
                "Excellent", 
                "Yes, inquire" 
        ));

// China Universities
        universities.add(new University(
                "cn001",
                "Tianjin Medical University",
                "Tianjin",
                "China",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A",
                "September",
                "English",
                "Public",
                R.drawable.img_tianjin_medicaluniversity,
                R.drawable.logo_tianjinmedical_uni,
                R.drawable.flag_china,
                "Medical",
                "Top 400",
                "Available"
        ));

        universities.add(new University(
                "cn002",
                "Zhejiang University",
                "Hangzhou",
                "China",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A+",
                "September",
                "English",
                "Public",
                R.drawable.img_zhejianguniversity,
                R.drawable.logo_zhejianguniversity,
                R.drawable.flag_china,
                "Medical",
                "Top 200",
                "Merit-based"
        ));

        universities.add(new University(
                "cn003",
                "Jilin University",
                "Changchun",
                "China",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A-",
                "September",
                "English",
                "Public",
                R.drawable.img_jilin_university,
                R.drawable.logo_jilinuniversity,
                R.drawable.flag_china,
                "Medical",
                "Top 600",
                "Check website"
        ));

        universities.add(new University(
                "cn004",
                "Shihezi University",
                "Shihezi",
                "China",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "B+",
                "September",
                "English",
                "Public",
                R.drawable.img_shihezi_university,
                R.drawable.logo_shiheziuniversity,
                R.drawable.flag_china,
                "Medical",
                "Good",
                "Inquire"
        ));

        universities.add(new University(
                "cn005",
                "Wuhan University",
                "Wuhan",
                "China",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A",
                "September",
                "English",
                "Public",
                R.drawable.img_wuhan_university,
                R.drawable.logo_wuhanuniversity,
                R.drawable.flag_china,
                "Medical",
                "Top 300",
                "Available"
        ));

        universities.add(new University(
                "cn006",
                "Fudan University",
                "Shanghai",
                "China",
                "General Medicine (MBBS)",
                "MD",
                "6 years",
                "A++",
                "September",
                "English",
                "Public",
                R.drawable.img_fudanuniversity,
                R.drawable.logo_fudanuniversity,
                R.drawable.flag_china,
                "Medical",
                "Top 100",
                "Highly competitive"
        ));


        // Kazakhstan Universities
        universities.add(new University(
                "kz001",
                "Kazakh Russian Medical University",
                "Almaty",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "B+",
                "September",
                "Russian/English",
                "Private",
                R.drawable.img_kazak_russianuniversity,
                R.drawable.logo_kazak_russiauniversity,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Good",
                "Available"
        ));

        universities.add(new University(
                "kz002",
                "West Kazakhstan Marat Ospanov State Medical University",
                "Aktobe",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "B",
                "September",
                "Kazakh/Russian",
                "Public",
                R.drawable.img_westkazak_marat_ospanovstateuniversity,
                R.drawable.logo_west_kazakshthan_marat_university,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Average",
                "Check website"
        ));

        universities.add(new University(
                "kz003",
                "South Kazakhstan State Medical Academy, Kazakhstan",
                "Shymkent",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "A-",
                "September",
                "Kazakh/Russian",
                "Public",
                R.drawable.img_southkazak_medicalacademy,
                R.drawable.logo_south_kazanacademy,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Very Good",
                "Limited"
        ));

        universities.add(new University(
                "kz004",
                "Semey State Medical University",
                "Semey",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "B",
                "September",
                "Kazakh/Russian/English",
                "Public",
                R.drawable.img_semey_statemedical_university,
                R.drawable.logo_semeystateuniver,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Good",
                "Available"
        ));

        universities.add(new University(
                "kz005",
                "Astana Medical University",
                "Astana",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "A",
                "September",
                "Kazakh/Russian/English",
                "Public",
                R.drawable.img_astana_medicaluniversity,
                R.drawable.logo_astana_mediuniversity,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Excellent",
                "Yes"
        ));

        universities.add(new University(
                "kz006",
                "Kokshetau State University",
                "Kokshetau",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "C+",
                "September",
                "Kazakh/Russian",
                "Public",
                R.drawable.img_kokshetau_stateuniversity,
                R.drawable.logo_kokshetau_uni,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Fair",
                "Check details"
        ));

        universities.add(new University(
                "kz007",
                "Karaganda State Medical University",
                "Karaganda",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "A-",
                "September",
                "Kazakh/Russian/English",
                "Public",
                R.drawable.img_karagandastate_medicaluniversity,
                R.drawable.logo_karaganda_satemedi_univer,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Very Good",
                "Available"
        ));

        universities.add(new University(
                "kz008",
                "North Kazakhstan State University",
                "Petropavl",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "C",
                "September",
                "Kazakh/Russian",
                "Public",
                R.drawable.img_northkazakh_statemedicaluniversity,
                R.drawable.logo_northwestern_stateuniversity,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Average",
                "N/A"
        ));

        universities.add(new University(
                "kz009",
                "Al-Farabi Kazakh National University",
                "Almaty",
                "Kazakhstan",
                "General Medicine (MBBS)",
                "MD",
                "5 years",
                "A",
                "September",
                "Kazakh/Russian/English",
                "Public",
                R.drawable.img_al_farabi_kazakhnationaluniversity,
                R.drawable.logo_al_farbiuniverisity,
                R.drawable.flag_kazakhstan,
                "Medical",
                "Top 500",
                "Competitive"
        ));


        //Nepal Universities
        universities.add(new University(
                "np001",
                "Kathmandu University",
                "Kathmandu",
                "Nepal",
                "General Medicine (MBBS)",
                "MBBS",
                "5.5 years",
                "A",
                "September",
                "English",
                "Private",
                R.drawable.img_kathmandu_university,
                R.drawable.logo_kathmandu_univer,
                R.drawable.flag_nepal,
                "Medical",
                "Good",
                "Available"
        ));

        universities.add(new University(
                "np002",
                "Tribhuvan University",
                "Kathmandu",
                "Nepal",
                "General Medicine (MBBS)",
                "MBBS",
                "5.5 years",
                "B+",
                "September",
                "English",
                "Public",
                R.drawable.img_tribhuvan_university,
                R.drawable.logo_tribhuvan_university,
                R.drawable.flag_nepal,
                "Medical",
                "Average",
                "Check Website"
        ));

        universities.add(new University(
                "np003",
                "Institute of Medicine",
                "Kathmandu",
                "Nepal",
                "General Medicine (MBBS)",
                "MBBS",
                "5.5 years",
                "A-",
                "September",
                "English",
                "Public",
                R.drawable.img_institue_of_medicine,
                R.drawable.logo_institue_of_medicine,
                R.drawable.flag_nepal,
                "Medical",
                "Very Good",
                "Limited"
        ));



        return universities;
    }
}