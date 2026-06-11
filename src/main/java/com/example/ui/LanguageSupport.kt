package com.example.ui

object Lang {
    private var isEnglish = true

    fun setLanguage(english: Boolean) {
        isEnglish = english
    }

    fun get(en: String, bn: String): String {
        return if (isEnglish) en else bn
    }

    // Common UI terms
    val appName get() = get("Poly Jewellers", "পলি জুয়েলার্স")
    val languageLabel get() = get("English / বাংলা", "বাংলা / English")
    val toggleTo get() = get("বাংলায় দেখুন", "Switch to English")
    val adminRole get() = get("Admin", "অ্যাডমিন")
    val staffRole get() = get("Staff", "স্টাফ")
    val currentRoleLabel get() = get("Current Role: ", "বর্তমান রোল: ")
    val searchPlaceholder get() = get("Search by Name or Mobile No...", "নাম বা মোবাইল নম্বর দিয়ে খুঁজুন...")
    val activeRole get() = get("Active Permissions Role", "সক্রিয় পারমিশন রোল")
    
    // Dashboard Summary Cards
    val dbTodayOrders get() = get("Today's Orders", "আজকের অর্ডার")
    val dbActiveOrders get() = get("Active Orders", "চলতি অর্ডার")
    val dbCompletedOrders get() = get("Completed Orders", "সম্পন্ন অর্ডার")
    val dbGoldOrders get() = get("Gold Orders", "স্বর্ণের অর্ডার")
    val dbSilverOrders get() = get("Silver Orders", "রুপার অর্ডার")
    val dbTotalCustomers get() = get("Total Customers", "মোট কাস্টমার")
    val dbTotalSales get() = get("Total Sales", "মোট বিক্রি")
    val dbTotalDue get() = get("Total Due", "মোট বকেয়া")
    
    // Menu Tabs
    val tabDashboard get() = get("Dashboard", "ড্যাশবোর্ড")
    val tabCustomers get() = get("Customers", "গ্রাহক")
    val tabOrders get() = get("Orders", "অর্ডারসমূহ")
    val tabRates get() = get("Rate Manager", "রেট ম্যানেজার")
    val tabReports get() = get("Reports", "রিপোর্ট")

    // Rates Screen
    val rateManagerTitle get() = get("Admin Gold & Silver Rates", "অ্যাডমিন সোনা ও রুপার রেট")
    val latestRates get() = get("Live Metal Market Rates (Per Vori)", "লাইভ মেটাল মার্কেট রেট (প্রতি ভরি)")
    val editGoldRates get() = get("Update Daily Gold Rates", "দৈনিক স্বর্ণের রেট আপডেট করুন")
    val editSilverRates get() = get("Update Daily Silver Rates", "দৈনিক রৌপ্যের রেট আপডেট করুন")
    val voriUnit get() = get("Vori", "ভরি")
    val anaUnit get() = get("Ana", "আনা")
    val ratiUnit get() = get("Rati", "রতি")
    val karat22 get() = get("22 Karat", "২২ ক্যারেট")
    val karat21 get() = get("21 Karat", "২১ ক্যারেট")
    val karat18 get() = get("18 Karat", "১৮ ক্যারেট")
    val silver925 get() = get("925 Silver (Chandi)", "৯২৫ রূপা (চান্দি)")
    val silver900 get() = get("900 Silver (Rupa)", "৯০০ রূপা (রুপা)")
    val silverOther get() = get("Other Silver", "অন্যান্য রূপা")
    val saveSettings get() = get("Save Rates", "রেট সংরক্ষণ করুন")
    val saveSuccess get() = get("Rates updated successfully!", "রেট সফলভাবে আপডেট করা হয়েছে!")

    // Customer Form & Fields
    val customerId get() = get("Customer ID", "কাস্টমার আইডি")
    val addNewCustomer get() = get("Register Customer", "নতুন গ্রাহক নিবন্ধন")
    val editCustomer get() = get("Edit Customer Profile", "কাস্টমার প্রোফাইল সম্পাদন")
    val fullName get() = get("Full Name", "পূর্ণ নাম")
    val mobileNumber get() = get("Mobile Number (Unique)", "মোবাইল নম্বর (ইউনিক)")
    val district get() = get("District", "জেলা")
    val thana get() = get("Thana", "থানা")
    val village get() = get("Village", "গ্রাম")
    val fullAddress get() = get("Full Address", "সম্পূর্ণ ঠিকানা")
    val fingerSize get() = get("Finger Size", "আঙুলের সাইজ")
    val ringSize get() = get("Ring Size", "আংটির সাইজ")
    val handSize get() = get("Hand/Wrist Size", "হাতের/কবজির সাইজ")
    val notes get() = get("General Notes", "সাধারণ নোটস")
    val saveCustomer get() = get("Save Customer", "গ্রাহক সংরক্ষণ")
    val errorMobileExists get() = get("A customer with this mobile number already exists!", "এই মোবাইল নম্বরের গ্রাহক ইতিমধ্যে নিবন্ধিত!")

    // Customer Detail & History
    val customerProfile get() = get("Customer Profile & History", "গ্রাহক প্রোফাইল এবং ইতিহাস")
    val statsTotalOrders get() = get("Total Orders", "মোট অর্ডার")
    val statsActiveOrders get() = get("Active Orders", "চলতি অর্ডার")
    val statsCompletedOrders get() = get("Completed Orders", "সম্পন্ন অর্ডার")
    val statsTotalAdvance get() = get("Total Advance Paid", "মোট অগ্রিম জমা")
    val statsTotalDue get() = get("Total Due", "মোট বকেয়া")
    val statsValue get() = get("Total Business Value", "মোট ব্যবসায়িক মূল্য")
    val createOrderBtn get() = get("Create New Order (Repeat-Ready)", "নতুন অর্ডার তৈরি করুন")
    val timelineTitle get() = get("Chronological History Timeline", "ক্রমানুসার ইতিহাস টাইমলাইন")
    val recentPayments get() = get("Payments Recorded", "পরিশোধের বিবরণ")
    val recentDeliveries get() = get("Deliveries Dispatched", "ডেলিভারি বিবরণ")

    // Order Form
    val orderId get() = get("Order ID", "অর্ডার আইডি")
    val addNewOrder get() = get("Create Custom Order", "নতুন আকর্ষক অর্ডার তৈরি")
    val editOrder get() = get("Modify Order Details", "অর্ডার পরিবর্তন করুন")
    val jewelryTypeLabel get() = get("Jewelry Material Type", "অলংকারের ধাতু টাইপ")
    val ptypeCategory get() = get("Product Category", "পণ্যের ধরণ")
    val orderSource get() = get("Order Acquisition Source", "অর্ডার পাওয়ার উৎস")
    val karatGradeSelection get() = get("Purity / Grade Quality", "বিশুদ্ধতা / গ্রেড মান")
    val weightSectionTitle get() = get("Jewelry Weight (Vori-Ana-Rati)", "অলংকারের ওজন (ভরি-আনা-রতি)")
    val currentRateApplied get() = get("Applied Conversion Rate (BDT)", "প্রযোজ্য রূপান্তর হার (টাকা)")
    val extraCosts get() = get("Additional Charges & Material Costs", "অতিরিক্ত চার্জ এবং উপাদান খরচ")
    val makingCharge get() = get("Making Charge (মজুরি)", "মজুরি (মেকিং চার্জ)")
    val stoneCost get() = get("Stone Cost / Gems Price", "পাথরের দাম (স্টোন কস্ট)")
    val otherMaterialCost get() = get("Other Accessories Cost", "অন্যান্য প্রয়োজনীয় খরচ")
    val financeHeader get() = get("Automated Invoice Calculations", "স্বয়ংক্রিয় বিল হিসাব")
    val calculatedMetalPrice get() = get("Raw Metal Value", "ধাতুর আনুমানিক দাম")
    val calculatedTotalBill get() = get("Calculated Total Bill", "সর্বমোট বিল (টাকা)")
    val initialAdvancePaid get() = get("Deposit Paid (Advance)", "অগ্রিম পরিশোধ (টাকা)")
    val dueRemaining get() = get("Remaining Due Amount", "অবশিষ্ট বকেয়া পরিমাণ")
    val selectCustomerFirst get() = get("Select Registered Customer", "নিবন্ধিত গ্রাহক নির্বাচন করুন")
    val orderSpecialNotes get() = get("Special Instructions & Requirements", "বিশেষ নির্দেশাবলী ও চাহিদা")
    val createOrderSubmit get() = get("Register Order", "অর্ডার বুকিং নিশ্চিত করুন")

    // Order Dropdowns Values
    val typeGold get() = get("Gold (স্বর্ণ)", "স্বর্ণ (Gold)")
    val typeSilver get() = get("Silver (রুপা)", "রুপা (Silver)")

    // Product Type Dropdowns Values
    val prodRing get() = get("Ring (আংটি)", "আংটি (Ring)")
    val prodChain get() = get("Chain (চেইন)", "চেইন (Chain)")
    val prodNecklace get() = get("Necklace (নেকলেস)", "নেকলেস (Necklace)")
    val prodBangle get() = get("Bangle (বালা/চুড়ি)", "বালা/চুড়ি (Bangle)")
    val prodBracelet get() = get("Bracelet (ব্রেসলেট)", "ব্রেসলেট (Bracelet)")
    val prodEarring get() = get("Earring (কানের দুল)", "কানের দুল (Earring)")
    val prodPendant get() = get("Pendant (লকেট)", "লকেট (Pendant)")
    val prodNosePin get() = get("Nose Pin (নাকফুল)", "নাকফুল (Nose Pin)")
    val prodCustom get() = get("Custom Design (কাস্টম ডিজাইন)", "কাস্টম ডিজাইন (Custom)")

    // Sources Dropdowns Values
    val srcMessenger get() = get("Messenger", "মেসেঞ্জার (Messenger)")
    val srcWhatsApp get() = get("WhatsApp", "হোয়াটসঅ্যাপ (WhatsApp)")
    val srcImo get() = get("IMO", "ইমো (IMO)")
    val srcTikTok get() = get("TikTok", "টিকটক (TikTok)")
    val srcShop get() = get("Physical Shop", "শোরুম / দোকান (Physical Shop)")

    // Order Status
    val statusNew get() = get("New Order", "নতুন অর্ডার")
    val statusConfirmed get() = get("Confirmed", "নিশ্চিতকৃত")
    val statusProduction get() = get("In Production", "কারখানায় নির্মাণাধীন")
    val statusReady get() = get("Ready for Delivery", "উত্তোলনের জন্য প্রস্তুত")
    val statusDelivered get() = get("Delivered", "ডেলিভারি সম্পন্ন")
    val statusCancelled get() = get("Cancelled", "বাতিলকৃত")

    // Payment Form/Status
    val recordPayment get() = get("Record Due Payment", "বকেয়া বিল পরিশোধ")
    val advanceAmountLabel get() = get("Amount Received", "পরিশোধিত টাকা")
    val paymentMethodLabel get() = get("Payment Gateway/Method", "পেমেন্ট মাধ্যম")
    val payNotes get() = get("Transaction Reference / Notes", "লেনদেন বিবরণ / নোট")
    val submitPayment get() = get("Submit Payment", "পেমেন্ট সম্পন্ন করুন")

    // Deliveries
    val dispatchDelivery get() = get("Dispatch Delivery Parcel", "পার্সেল ডেলিভারি পাঠান")
    val courierNameLabel get() = get("Courier / Logistics Name", "কুরিয়ার সার্ভিসের নাম")
    val parcelIdLabel get() = get("Consignment Parcel Tracking ID", "পার্সেল ট্র্যাকিং আইডি")
    val deliveryNotesLabel get() = get("Logistics Notes (E.g. address details)", "ডেলিভারি বিবরণ বা নোটস")
    val submitDelivery get() = get("Register Dispatch", "ডেলিভারি বুক করুন")

    // Reports Screen
    val reportsHeader get() = get("Business Performance Reporting Panel", "ব্যবসায়িক রিপোর্ট ও বিশ্লেষণ")
    val filterDateRange get() = get("Filter by Date Duration", "তারিখের ব্যাপ্তি দিয়ে ফিল্টার")
    val filterCustomer get() = get("Filter by Customer", "নির্দিষ্ট কাস্টমার ফিল্টার")
    val filterProdType get() = get("Filter by Jewelry Category", "অলংকারের ধরণ ফিল্টার")
    val filterMetalType get() = get("Filter by Gold/Silver", "সোনা / রূপা ফিল্টার")
    val filterStatus get() = get("Filter by Delivery/Order Status", "ডেলিভারি / অর্ডার স্ট্যাটাস")
    val allLabel get() = get("All (সব)", "সব (All)")
    val runAnalyticsReport get() = get("Generate Custom Excel Report", "রিপোর্ট জেনারেট করুন")
    val statsReportTitle get() = get("Filtered Analytics Results", "ফিল্টারড রিপোর্ট ফলাফল")
    val totalRevenue get() = get("Total Generated Order Value", "মোট অর্ডার মূল্য")
    val totalOutstanding get() = get("Total Active Receivables / Due", "মোট আদায়যোগ্য বকেয়া")

    // Sheet Sync Sync Integration
    val googleSheetsSync get() = get("Google Sheets Live Sync", "গুগল শিট সিঙ্ক (Google Sheets Active)")
    val sheetsStatusOk get() = get("System connected. Synchronization maps orders cleanly.", "গুগল স্প্রেডশিটে ডেটা সিঙ্ক করা হয়েছে!")
    val copySheetsCsv get() = get("Export CSV Data", "কপি বা এক্সপোর্ট সিএসভি (CSV)")
    val roleRestrictionText get() = get("Access Restricted. This section requires Administrative level credentials.", "প্রবেশাধিকার সংরক্ষিত। এই বিভাগটি দেখতে অ্যাডমিন পারমিশন প্রয়োজন।")
}
