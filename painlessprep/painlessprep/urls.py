from django.urls import path, include

urlpatterns = [
    path('', include('core.urls')),   # 👈 link to your app's urls.py
]