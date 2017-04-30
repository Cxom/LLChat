package me.cxom.llchat.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListPaginationTest {
    @Test
    public void paginateTest() {
        List<List<String>> languagePages = new ArrayList<>();
        String[] langs = new String[]{"Akan", "Amharic", "Arabic", "Assamese",
                "Awadhi", "Azerbaijani", "Balochi", "Belarusian", "Bengali",
                "Bhojpuri", "Burmese", "Cantonese", "Cebuano", "Chewa",
                "Chhattisgarhi", "Chittagonian", "Czech", "Deccan", "Dhundhari",
                "Dutch", "Eastern Min", "English", "Esperanto", "French",
                "Fula", "Fuzhounese", "Gan", "German", "Greek", "Gujarati",
                "Haitian Creole", "Hakka", "Haryanvi", "Hausa", "Hiligaynon",
                "Hindi", "Hmong", "Hokkien", "Hungarian", "Hunnanese", "Ido",
                "Igbo", "Ilocano", "Ilonggo", "Indonesian", "Interlingua",
                "Italian", "Japanese", "Javanese", "Jin", "Kannada", "Kazakh",
                "Khmer", "Kinyarwanda", "Kirundi", "Konkani", "Korean",
                "Kurdish", "Lao", "Latin", "Madurese", "Magahi", "Maithili",
                "Malagasy", "Malay", "Malayalam", "Malaysian", "Mandarin",
                "Marathi", "Marwari", "Mossi", "Nepali", "Northern Min", "Odia",
                "Oriya", "Oromo", "Pashto", "Persian", "Polish", "Portuguese",
                "Punjabi", "Quechua", "Romanian", "Russian", "Saraiki",
                "Serbo-Croatian", "Shanghainese", "Shona", "Sindhi",
                "Sinhalese", "Somali", "Southern Min", "Spanish", "Sundanese",
                "Swedish", "Sylheti", "Tagalog", "Tamil", "Telugu", "Teochew",
                "Thai", "Turkish", "Turkmen", "Ukrainian", "Urdu", "Uyghur",
                "Uzbek", "Vietnamese", "Wu", "Xhosa", "Xiang", "Yoruba", "Yue",
                "Zhuang", "Zulu"};

        List<String> languages = Arrays.asList(langs);
        for (int i = 0; i < languages.size(); i += 10) {
            int endid = (i + 9) > languages.size() ? languages.size() : i + 9;
            languagePages.add(languages.subList(i, endid));
            if ((i + 9) > languages.size()) break;
        }
        Assert.assertEquals("Akan", languagePages.get(0).get(0));

        int pninput = 1;

        int pages = languagePages.size();
        int arg1 = pninput-1;
        int pn = Math.max(0, Math.min(pages, arg1));
        List<String> page = languagePages.get(pn);
        Assert.assertEquals("Akan", page.get(0));
    }
}
