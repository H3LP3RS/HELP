
<h1 align="center">
  H3LP!
 <br>
  <a href="https://github.com/H3LP3RS/HELP"><img src="https://github.com/H3LP3RS/HELP/blob/main/app/src/main/res/drawable/heart_link.png" width="300"></a>
  <br>
</h1>

<h2 align="center">Anyone can be an hero.</h2>

<p align="center">

  <a href="https://github.com/H3LP3RS/HELP/commit/main">
    <img src="https://img.shields.io/github/last-commit/H3LP3RS/HELP"
        alt="Last commit">
  </a>

  <a href="https://github.com/H3LP3RS/HELP/graphs/contributors">
      <img src="https://img.shields.io/github/contributors/H3LP3RS/HELP"
       alt="Contributors">
  </a>

 
 
  <a href="https://cirrus-ci.com/github/H3LP3RS/HELP">
    <img src="https://api.cirrus-ci.com/github/H3LP3RS/HELP.svg"
         alt="Build Status">
  </a>
    
  <a href="https://codeclimate.com/github/H3LP3RS/HELP/test_coverage">
    <img src="https://api.codeclimate.com/v1/badges/dbecacba890747624c24/test_coverage"
         alt="Test coverage">
  </a>
    
  <a href="https://codeclimate.com/github/H3LP3RS/HELP/maintainability">
    <img src="https://api.codeclimate.com/v1/badges/dbecacba890747624c24/maintainability"
         alt="Mainttainability">
  </a>
    
</p>

<p align="center">
  <a href="#key-features">Key Features</a> •
  <a href="https://www.youtube.com/watch?v=d6ds5Lch3GA">Presentation video</a> •
  <a href="#how-to-use">How To Use</a> •
  <a href="#screenshots">Screenshots</a> •
  <a href="#the-team">The team</a>
</p>

The purpose of this app is to offer emergency help by allowing willing users to register and enter their skills (e.g. have taken courses on first aid) and the first aid medicine they usually walk around with (eg. EpiPens for people with severe allergies, asthma inhalers for people with asthma, antihistamines for allergies…).

When a user suddenly needs their medication (e.g. if they had an anaphylactic shock), after calling an ambulance, them (or the people they are with) can go on the app and ask for a certain medication or for someone with a particular first aid skill, the app will then notify any matching users in a certain radius so that they can come and help.

It was created as part of the Software Development Project [CS-306](https://edu.epfl.ch/coursebook/en/software-development-project-CS-306-1)
class at EPFL.

## Key Features

Once the app is downloaded the users can sign in and fill up their information:
- signe in with Google or anonymously. 
- Fill up there medical informing, containing useful information in case of emergency like blood type, allergy or current Treatment.
- Add emergency contact to call in case of emergency.
- Add there medical skills and the medicine they carry with them.

That's it they are ready to save and be saved.

In case of an emergency the use :
- tap the H3LP button and select what help he/she need
- can decide to call the emergency service or her/his emergency contact
- chat with the helpers that are comming for her/him  to give them more information


## Other Features

H3LP is a lot more than that and contains many features to keep you safe:
- A map to locate nearby hospitals, pharmacies and defibrillators
- Many tutorials to learn first aid tips with videos
- A simple tool that gives you the best tempo to do CPR
- A forum where user can ask medical professional for tips and information by topic

## Privacy Policy

H3LP is concerned about privacy of its user, especially when it comes to sensitive information such as medical information. So H3LP guarantee that all conversation are end-to-end encrypted and let every user choose what data should be synchronised online.

## How To Use

1. Clone this repositories and open it in any IDE. (Recommended AndroidStudio)
2. Add your own API key for google map in values/google_maps_api.xml
3. Add your own Firebase URL in com/github/h3lp3rs/h3lp/model/database/FireDatabase.kt
4. If you want reactivate the Animation for the CPR activity (removed because of continous integration). To do so remove commented line in com/github/h3lp3rs/h3lp/view/mainpage/CprRateActivity.kt




## The team
- Wiam Eddahri (wiam.eddahri@epfl.ch)
- Hind El Bouchrifi (hind.elbouchrifi@epfl.ch)
- Adrien Nelson Rey (adrien.rey@epfl.ch)
- Alexis Schlomer (alexis.schlomer@epfl.ch)
- Alexandre Michel Hayderi (alexandre.hayderi@epfl.ch)
- Emilien Duc (emilien.duc@epfl.ch)


## Screenshots

<p align="center">

  <img src="/screenshots/Intro.png"  width="200"/>
  <img src="/screenshots/home.png"  width="200"/>
   <img src="/screenshots/skills.png"  width="200"/>
  <img src="/screenshots/help.png"  width="200"/>
  
</p>
<p align="center">

  <img src="/screenshots/Helper.png"  width="200"/>
  <img src="/screenshots/Helpee.png"  width="200"/>
   <img src="/screenshots/chat.png"  width="200"/>
  <img src="/screenshots/CPR.png"  width="200"/>
  
</p>

<p align="center">
  <img src="/screenshots/tuto.png"  width="200"/>
   <img src="/screenshots/Professional.png"  width="200"/>
  <img src="/screenshots/Forum.png"  width="200"/>
  
</p>

