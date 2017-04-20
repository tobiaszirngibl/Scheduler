from django.contrib.auth.decorators import login_required
from django.http import HttpResponseRedirect
from django.shortcuts import redirect, render, get_object_or_404

from backend.models import Actor

from .forms import AvatarForm


# Create your views here.

@login_required
def profile_view(request):
	user = get_object_or_404(Actor, id=request.user.id)
	user.understudy_name = user.understudy.email
	return render(request, 'website/profile.html', {"user": user})


@login_required
def calendar_view(request):
	return render(request, 'website/calendar.html', {"user": request.user})


@login_required
def group_view(request):
	return render(request, 'website/groups.html', {"user": request.user})


@login_required
def login_redirect(request):
	return HttpResponseRedirect('/accounts/private_profile/%d/' % request.user.id)


@login_required
def upload_avatar(request):
	if request.method == 'POST':
		form = AvatarForm(request.POST, request.FILES)
		if form.is_valid():
			request.user.avatar = request.FILES['avatar']
			request.user.save()

	return redirect('profile_view')
