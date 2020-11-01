"""geocode URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.1/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path

from .views import UsersView, GPSView, SymptomsView, PingView

urlpatterns = [
    path('admin/', admin.site.urls),

    path('ping/', PingView.as_view()),

    # Endpoints for users URL.
    path('user/', UsersView.as_view(), name='users'),
    path('user/<int:uuid>/', UsersView.as_view(), name='users'),

    # Endpoints for gps URL.
    path('gps/', GPSView.as_view(), name='gps'),
    path('gps/<int:uuid>/', GPSView.as_view(), name='gps'),

    # Endpoints for symptoms URL.
    path('symptoms/', SymptomsView.as_view(), name='symptoms'),
    path('symptoms/<int:uuid>/', SymptomsView.as_view(), name='symptoms'),
    ]