import os
import pandas as pd
from datetime import date, datetime, timedelta
from django.conf import settings
from django.shortcuts import render, redirect
from django.contrib.auth.models import User



from django.shortcuts import render
import subprocess


def home(request):
    context = {
        'name': 'Django User'
    }
    return render(request, 'home.html', context)


def run_calibration(request):
    output = None
    error = None

    if request.method == 'POST':
        script_path = os.path.join(settings.BASE_DIR, 'core', 'scripts', 'calibrate_camera.py')

        try:
            result = subprocess.run(
                ["python3", script_path],
                capture_output=True,
                text=True,
                timeout=30
            )
            output = result.stdout or "Script ran but produced no output."
            error = result.stderr if result.returncode != 0 else None

        except subprocess.TimeoutExpired:
            error = "Script timed out."
        except FileNotFoundError:
            error = f"Script not found at: {script_path}"
        except Exception as e:
            error = str(e)

    return render(request, 'run_calibration.html', {'output': output, 'error': error})

def run_detection(request):
    output = None
    error = None

    if request.method == 'POST':
        script_path = os.path.join(settings.BASE_DIR, 'core', 'scripts', 'MarkerDetection.py')

        try:
            result = subprocess.run(
                ["python3", script_path],
                capture_output=True,
                text=True,
                timeout=30
            )
            output = result.stdout or "Script ran but produced no output."
            error = result.stderr if result.returncode != 0 else None

        except subprocess.TimeoutExpired:
            error = "Script timed out."
        except FileNotFoundError:
            error = f"Script not found at: {script_path}"
        except Exception as e:
            error = str(e)

    return render(request, 'run_calibration.html', {'output': output, 'error': error})