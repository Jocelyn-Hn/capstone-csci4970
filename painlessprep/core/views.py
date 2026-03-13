import os
import sys
import subprocess
from django.conf import settings
from django.shortcuts import render
from django.http import HttpResponse


def home(request):
    return render(request, 'home.html')


def run_calibration(request):
    output = None
    error = None

    if request.method == 'POST':

        script_path = os.path.join(
            settings.BASE_DIR,
            "core",
            "scripts",
            "calibrate_camera.py"
        )

        try:
            result = subprocess.run(
                [sys.executable, script_path],
                capture_output=True,
                text=True,
                timeout=30
            )

            output = result.stdout or "Script ran but produced no output."
            error = result.stderr if result.returncode != 0 else None

        except Exception as e:
            error = str(e)

    return render(request, 'run_calibration.html', {'output': output, 'error': error})


def run_detection(request):
    output = None
    error = None

    if request.method == 'POST':

        script_path = os.path.join(
            settings.BASE_DIR,
            "core",
            "scripts",
            "MarkerDetection.py"
        )

        try:
            result = subprocess.run(
                [sys.executable, script_path],
                capture_output=True,
                text=True,
                timeout=30
            )

            output = result.stdout or "Script ran but produced no output."
            error = result.stderr if result.returncode != 0 else None

        except Exception as e:
            error = str(e)

    return render(request, 'run_calibration.html', {'output': output, 'error': error})