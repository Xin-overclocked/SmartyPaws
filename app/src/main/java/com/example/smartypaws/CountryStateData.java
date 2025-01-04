package com.example.smartypaws;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CountryStateData {

    private static final Map<String, String[]> countryStateMap = new TreeMap<>();

    static {
        // Add countries and their states/provinces
        countryStateMap.put("Bangladesh", new String[]{"Dhaka", "Chittagong", "Khulna"});
        countryStateMap.put("China", new String[]{"Beijing", "Shanghai", "Guangdong", "Sichuan", "Zhejiang"});
        countryStateMap.put("Japan", new String[]{"Tokyo", "Osaka", "Kyoto", "Hokkaido", "Fukuoka"});
        countryStateMap.put("South Korea", new String[]{"Chungcheongbuk-do (North Chungcheong)", "Chungcheongnam-do (South Chungcheong)", "Gangwon-do", "Gyeonggi-do", "Gyeongsangbuk-do (North Gyeongsang)", "Gyeongsangnam-do (South Gyeongsang)", "Jeollabuk-do (North Jeolla)", "Jeollanam-do (South Jeolla)", "Jeju-do (Special Self-Governing Province)", "Seoul", "Busan", "Daegu", "Daejeon", "Gwangju", "Incheon", "Ulsan"});
        countryStateMap.put("Pakistan", new String[]{"Punjab", "Sindh", "Khyber Pakhtunkhwa", "Balochistan", "Islamabad"});
        countryStateMap.put("Philippines", new String[]{"Metro Manila", "Cebu", "Davao", "Baguio", "Palawan"});
        countryStateMap.put("Vietnam", new String[]{"Hanoi", "Ho Chi Minh City", "Da Nang", "Hai Phong", "Can Tho"});
        countryStateMap.put("Thailand", new String[]{"Bangkok", "Chiang Mai", "Phuket", "Pattaya", "Krabi"});
        countryStateMap.put("Malaysia", new String[]{"Johor", "Kedah", "Kelantan", "Malacca", "Negeri Sembilan", "Pahang", "Penang", "Perak", "Perlis", "Sabah", "Sarawak", "Selangor", "Terengganu", "Kuala Lumpur", "Labuan", "Putrajaya"});
        countryStateMap.put("Singapore", new String[]{"Central Singapore"}); // No states, unitary city-state
        countryStateMap.put("Sri Lanka", new String[]{"Colombo", "Kandy", "Galle", "Jaffna", "Anuradhapura"});
        countryStateMap.put("Myanmar", new String[]{"Yangon", "Mandalay", "Naypyidaw", "Bago", "Sagaing"});
        countryStateMap.put("Nepal", new String[]{"Kathmandu", "Pokhara", "Lumbini", "Chitwan", "Biratnagar"});
        countryStateMap.put("Afghanistan", new String[]{"Kabul", "Herat", "Kandahar", "Mazar-i-Sharif", "Jalalabad"});
        countryStateMap.put("Iran", new String[]{"Tehran", "Isfahan", "Mashhad", "Shiraz", "Tabriz"});
        countryStateMap.put("Iraq", new String[]{"Baghdad", "Basra", "Mosul", "Erbil", "Najaf"});
        countryStateMap.put("Saudi Arabia", new String[]{"Riyadh", "Jeddah", "Mecca", "Medina", "Dammam"});
        countryStateMap.put("Turkey", new String[]{"Istanbul", "Ankara", "Izmir", "Bursa", "Antalya"});
        countryStateMap.put("United Arab Emirates", new String[]{"Abu Dhabi", "Dubai", "Sharjah", "Ajman", "Fujairah"});
        countryStateMap.put("Kazakhstan", new String[]{"Almaty", "Nur-Sultan", "Shymkent", "Karaganda", "Aktobe"});
        countryStateMap.put("Uzbekistan", new String[]{"Tashkent", "Samarkand", "Bukhara", "Andijan", "Namangan"});
        countryStateMap.put("Maldives", new String[]{"Malé"}); // No states, unitary archipelago
        countryStateMap.put("Bhutan", new String[]{"Thimphu", "Paro", "Punakha", "Wangdue Phodrang", "Phuntsholing"});
        countryStateMap.put("Mongolia", new String[]{"Ulaanbaatar", "Erdenet", "Darkhan", "Choibalsan", "Zuunmod"});
        countryStateMap.put("Laos", new String[]{"Vientiane", "Luang Prabang", "Pakse", "Savannakhet", "Phonsavan"});
        countryStateMap.put("Cambodia", new String[]{"Phnom Penh", "Siem Reap", "Sihanoukville", "Battambang", "Kampot"});
        countryStateMap.put("North Korea", new String[]{"Pyongyang", "Kaesong", "Wonsan", "Hamhung", "Sinuiju"});
        countryStateMap.put("Brunei", new String[]{"Bandar Seri Begawan"}); // No states, small unitary country
        countryStateMap.put("United States", new String[]{"California", "Texas", "Florida", "New York", "Illinois", "Pennsylvania", "Ohio", "Georgia", "North Carolina", "Michigan"});
        countryStateMap.put("Canada", new String[]{"Ontario", "Quebec", "British Columbia", "Alberta", "Manitoba", "Saskatchewan", "Nova Scotia", "New Brunswick", "Prince Edward Island", "Newfoundland and Labrador"});
        countryStateMap.put("Russia", new String[]{"Moscow", "Saint Petersburg", "Krasnoyarsk", "Novosibirsk", "Yekaterinburg", "Nizhny Novgorod", "Kazan", "Samara", "Omsk", "Vladivostok"});
        countryStateMap.put("Germany", new String[]{"Bavaria", "Berlin", "North Rhine-Westphalia", "Hesse", "Saxony", "Baden-Württemberg", "Lower Saxony", "Rhineland-Palatinate", "Thuringia", "Schleswig-Holstein"});
        countryStateMap.put("France", new String[]{"Île-de-France", "Provence-Alpes-Côte d'Azur", "Auvergne-Rhône-Alpes", "Brittany", "Normandy", "New Aquitaine", "Occitanie", "Grand Est", "Hauts-de-France", "Pays de la Loire"});
        countryStateMap.put("United Kingdom", new String[]{"England", "Scotland", "Wales", "Northern Ireland"});
        countryStateMap.put("Australia", new String[]{"New South Wales", "Queensland", "Victoria", "Western Australia", "South Australia", "Tasmania", "Australian Capital Territory", "Northern Territory"});
        countryStateMap.put("Brazil", new String[]{"São Paulo", "Rio de Janeiro", "Bahia", "Minas Gerais", "Paraná", "Santa Catarina", "Rio Grande do Sul", "Pernambuco", "Ceará", "Goiás"});
        countryStateMap.put("Mexico", new String[]{"Jalisco", "Nuevo León", "Puebla", "Chihuahua", "Guanajuato", "Veracruz", "Yucatán", "Baja California", "Sonora", "Durango"});
        countryStateMap.put("Italy", new String[]{"Lazio", "Lombardy", "Veneto", "Campania", "Sicily", "Emilia-Romagna", "Tuscany", "Piedmont", "Apulia", "Calabria"});
        countryStateMap.put("Spain", new String[]{"Madrid", "Catalonia", "Andalusia", "Valencia", "Galicia", "Castile and León", "Basque Country", "Aragon", "Canary Islands", "Castilla-La Mancha"});
        countryStateMap.put("Argentina", new String[]{"Buenos Aires", "CABA", "Cordoba", "Santa Fe", "Mendoza", "Tucumán", "Chaco", "Misiones", "Salta", "Entre Ríos"});
        countryStateMap.put("South Africa", new String[]{"Gauteng", "Western Cape", "KwaZulu-Natal", "Eastern Cape", "Free State", "Limpopo", "Mpumalanga", "North West", "Northern Cape"});
        countryStateMap.put("Egypt", new String[]{"Cairo", "Alexandria", "Giza", "Luxor", "Aswan", "Sharm El Sheikh", "Port Said", "Suez", "Dakahlia", "Beheira"});
        countryStateMap.put("Nigeria", new String[]{"Lagos", "Kano", "Rivers", "Kaduna", "Oyo", "Katsina", "Anambra", "Enugu", "Borno", "Sokoto"});
        countryStateMap.put("India", new String[]{"Uttar Pradesh", "Maharashtra", "Bihar", "West Bengal", "Madhya Pradesh", "Tamil Nadu", "Rajasthan", "Karnataka", "Gujarat", "Andhra Pradesh"});
        countryStateMap.put("Indonesia", new String[]{"Jakarta", "Bali", "Java", "Sumatra", "Sulawesi", "Kalimantan", "Papua", "Maluku", "Nusa Tenggara"});
        countryStateMap.put("Poland", new String[]{"Mazowieckie", "Lublin", "Lubusz", "Lower Silesian", "Podlaskie", "Opole", "Pomeranian", "Silesian", "Warmian-Masurian", "Greater Poland"});
        countryStateMap.put("Netherlands", new String[]{"North Holland", "South Holland", "Utrecht", "Groningen", "Friesland", "Overijssel", "Gelderland", "Drenthe", "Limburg", "Zeeland"});
        countryStateMap.put("Belgium", new String[]{"Flanders", "Wallonia", "Brussels-Capital"});
        countryStateMap.put("Sweden", new String[]{"Stockholm", "Västra Götaland", "Skåne", "Östergötland", "Uppsala", "Värmland", "Halland", "Jönköping", "Örebro", "Kronoberg"});
        countryStateMap.put("Finland", new String[]{"Uusimaa", "Southern Finland", "Western Finland", "Northern Finland", "Lapland", "Åland Islands"});

    }

    /**
     * Get the list of all countries.
     *
     * @return String[] - Array of country names.
     */
    public static String[] getCountries() {
        return countryStateMap.keySet().toArray(new String[0]);
    }

    /**
     * Get the list of states for a given country.
     *
     * @param countryName - The name of the country.
     * @return String[] - Array of state names. Returns an empty array if country not found.
     */
    public static String[] getStates(String countryName) {
        return countryStateMap.getOrDefault(countryName, new String[]{});
    }
}

