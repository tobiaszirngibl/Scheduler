from django.contrib.auth.decorators import login_required
from django.shortcuts import redirect, render


# Create your views here.

def profile_view(request, user_id):
	user = {"name": request.user.email}
	return render(request, 'website/profile.html', {"user": user})

@login_required
def login_redirect(request):
	return redirect('profile/'+str(request.user.pk)+'/')

