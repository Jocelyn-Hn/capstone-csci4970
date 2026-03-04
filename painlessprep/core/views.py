import os
import pandas as pd
from datetime import date, datetime, timedelta
from django.conf import settings
from django.shortcuts import render, redirect
from django.contrib.auth.models import User


from django.shortcuts import render

def home(request):
    context = {
        'name': 'Django User'   # this gets passed to the template
    }
    return render(request, 'home.html', context)