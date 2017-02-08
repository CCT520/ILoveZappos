# ILoveZappos
In my project, I implemented searching products according to users input, displaying searching results on the screen, 
and viewing basic information of any items on the product page. And then in the items page, user can click product image 
to zoom the product, click the floating action button to add product in the cart that stays at the bottom of this page. 
Users can get more details about an item from product url just by clicking the button inside the action bar.

Also, by clicking action bar user can share a product to any other users nearby, just by creating an QR code image which 
will be displayed on the screen of item page. Similarly, user can scan QR code shared by other users to view the details 
of a product just by clicking camera icon at the left top of product page.(I think QR code is the best way to share a 
product to nearby friends without support from server)

Finally, all data was binded to corresponding page by using databinding; and REST requests were handled by retrofit;
both methods were used to handle configuration changes (Retaining an Object During a Configuration Change and 
Handling the Configuration Change Yourself). What's more,there is the other way was used to handle configuration changes---
getSharedPreferences. By invoking getSharedPreferences, the last request result would be stored in the App, even App was
exited by user, so that the next time the user open this App they would see the last request result showed on the product
page instead of a blank page.

This project can be seperated into three layers, the first I think can be called as controller including MainActivity,
ProductActivity, GetSharedProductActivity ScanCodeActivity and EventHandlers,this four Activity files connect data and 
user interface(Actually, I think RetainedFragment and RequestServers can be also regarded as controller). Of course the
data layer including, they are Product and RequestResult. The third layer I think is user layer including ListAdapter 
and all layout files.
