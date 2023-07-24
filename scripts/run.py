import os
import sys
import subprocess

app_repo_prefix = "/home/fengrunhan/hdd/APKCrawler/APKPure"


popular_list = [
    "com.affinity.rewarded_play",
    "com.android.chrome",
    "com.cbs.app",
    "com.discord",
    "com.disney.disneyplus",
    "com.facebook.katana",
    "com.facebook.orca",
    "com.google.android.apps.docs",
    "com.google.android.apps.maps",
    "com.google.android.apps.messaging",
    "com.google.android.apps.photos",
    "com.google.android.apps.tachyon",
    "com.google.android.apps.translate",
    "com.google.android.apps.youtube.music",
    "com.google.android.calculator",
    "com.google.android.calendar",
    "com.google.android.contacts",
    "com.google.android.deskclock",
    "com.google.android.dialer",
    "com.google.android.googlequicksearchbox",
    "com.google.android.keep",
    "com.indeed.android.jobsearch",
    "com.microsoft.office.outlook",
    "com.newleaf.app.android.victor",
    "com.netflix.mediaclient",
    "com.particlenews.newsbreak",
    "com.peacocktv.peacockandroid",
    "com.pinterest",
    "com.reddit.frontpage",
    "com.snapchat.android",
    "com.spotify.music",
    "com.twitter.android",
    "com.venmo",
    "com.verizon.messaging.vzmsgs",
    "com.vyroai.aiart",
    "com.walmart.android",
    "com.whatsapp",
    "us.zoom.videomeetings"
]

recent_list = [
    "ai.chat.gpt.bot",
    "ai.metaverselabs.obdandroid",
    "com.ai.polyverse.mirror",
    "com.amobi.barcode.qrcode.scanner",
    "com.booster.gameboostermega2",
    "com.codeway.chatapp",
    "com.dddev.gallery.album.photo.editor",
    "com.diavostar.pdfreader.documentscanner.documentviewer",
    "com.gaft.videoeraser",
    "com.ghosttube.seer",
    "com.hiplay.pet",
    "com.honeycam.pro",
    "com.hugelettuce.unidream.ai.drawing",
    "com.kiriengine.app",
    "com.korda.vpn",
    "com.liked.livenews",
    "com.lucrasports",
    "com.lutech.musicplayer",
    "com.measurement.com",
    "com.mwm.stems",
    "com.nyxs.star",
    "com.pawxy.browser",
    "com.photoeditor.beautycamera.facemakeup	Photo Editor",
    "com.scaleup.chatai",
    "com.seoulmetro.safety_keeper",
    "com.techconsolidated.photosolve",
    "com.upriselabs.stella",
    "com.videoplayer.mediaplayer.hdvideoplayer",
    "com.voidpet",
    "com.volumebooster.bassboster.sound.eq.equalizer.virtualizer.speaker",
    "com.yango.eats",
    "photo.to.anime.ai.animeai",
    "pic.collage.maker.photocollage"
]

def generate_app_path(app_repo, app):
    path = app_repo_prefix + '/' + app_repo + '/' + app
    return path

def run(app_repo, app):
    libpath = generate_app_path(app_repo, app)
    outpath = 'result/' + app_repo + '/' + app + '/record.log'
    cmd = f"java -jar APKDiff.jar -s sdk -d {libpath} -o result/{app_repo}"
    print(cmd)
    cmd_list = cmd.split(" ")
    with open(outpath, "a") as f:
        subprocess.run(cmd_list, stdout=f, stderr=subprocess.STFOUT)

def usage():
    print("python run.py [popular|recent]")
    exit()

if __name__ == "__main__":
    if len(sys.argv) != 2:
        usage()

    category = sys.argv[1]
    categories = ["popular", "recent"]
    if category not in categories:
        usage()

    if category == "popular":
        app_list = popular_list
        app_repo = "popular"
    else:
        app_list = recent_list
        app_repo = "recent"

    for app in app_list:
        print(app)
        run(app_repo, app)

