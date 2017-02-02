from django.contrib.auth.decorators import login_required
from django.shortcuts import redirect, render
from django.urls import reverse

# Create your views here.

def profile_view(request, user_id):
	return render(request, 'website/profile.html')

@login_required
def login_redirect(request):
	return redirect('profile/'+str(request.user.pk)+'/')