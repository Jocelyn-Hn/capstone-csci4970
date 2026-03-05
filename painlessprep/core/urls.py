from django.urls import path
from . import views

urlpatterns = [
    path('', views.home, name='home'),
    path('run/', views.run_calibration, name='run_calibration'),
    path('run/', views.run_detection, name='run_detection'),
]