Version 102

---------
 
Version Name: 5efa1f36-c813-4043-afab-32e64575efb0

haveged: set to fill at least 4088
bug: overcommit back to 50
mount: async journal ( please disable mount namespace separation to avail )
networking: updates
wifi supplicant: scan interval set to 180 due to overcrowding

$ busybox dd if=/dev/random of=/dev/null bs=64 count=64
64+0 records in
64+0 records out
4096 bytes (4.1 kB, 4.0 KiB) copied

$ sha256sum *.apk
ebc53d795f40a39a10140eec4a0c91a8d5bb15c5c52493a5b86e78b8bd6395a1 *102-waut.ch.apk
ebc53d795f40a39a10140eec4a0c91a8d5bb15c5c52493a5b86e78b8bd6395a1 *oi.apk

$ md5sum *.apk
e5664e31d8a02913938b9c94d3656e44 *102-waut.ch.apk
e5664e31d8a02913938b9c94d3656e44 *oi.apk

-----

Recommended: 
 - ntp: automatic system time update from internet is enabled. 
   please check the clock and fiddle around with the timezone settings in case of any issues. one may need to set the timezone manually.
   then simply run the app to initiate a time sync
   the network time sync happens at around 3am. so the time to check is in the morning. 
 - Please disable mount namespace separation in the superuser app to take advantage of the mount optimisations.
 - Reboot once and occasionally to reseed the entropy pool. It's good luck! 
 - Do ensure that the waut.ch service has started upon reboot. Just run if it doesn't start it automatically!

-----

Utility for background calibration, curation and tuning of the device towards an intuitive interface.

Subsystems being battery, entropy, encryption, disk, cpu, memory, filesystem, ui, scheduler, and network, all safe and open source technology.

Presented in this educational gaming metric format with infinite feedback and an interestingly assymetric chance. scribble anywhere, check in some stress, or find the 8!

- waut.ch! does one hope to receive from this?

Well, increasing degrees and amounts of a certain "Je ne sais quoi" or responsiveness from the user interface for a start. Better battery life perhaps. Better quality of life, maybe.

And waut.ch! can only perhaps be described as "A qualified quantification of the placebo effect"

waut.ch! might benefit from this?

In the Android device space:

- Designers
- Users
- Manufacturers
- Recyclers
- Developers
- Compilers
- Support personnel
- OEMs
- The Friendly Neighborhood Nerd/Technician.

“Make the most of yourself....for waut.ch! is all there is of you.” - Ralph Waldo Emerson ( paraphrase )

All along the waut.ch! tower - Bob Dylan

waut.ch! - Sometimes used in some colloquium as "watch!", keen upon reducing the TDP of mobile devices to 1.0 waut.ch!

-----

ARM32 variants of Android only Donut 1.6+

Please uninstall either Seeder or CrossBreeder prior to using this.

Root recommended, else reactivity metric is interesting and introduces uniqueness into the entropy pool anyway.  Metric may demonstrate a certain asymmetry that is expected from predictable human actions. Efforts have been made to remove time seed logic from haveged in order to improve upon encryption and system-wide performance and security.

Also numerous other subsystems require careful calibration to facilitate this process.

Rewritten code, subset of functionality for upstream project - CrossBreeder ( https://forum.xda-developers.com/showthread.php?t=2113150 )

Please feel free to view and analyze source and functionality and report bugs and discuss etc on the XDA forum:

( https://forum.xda-developers.com/android/apps-games/app-waut-ch-calibration-android-t3549967 )

Google Play store:

( https://play.google.com/store/apps/details?id=ch.waut )

Please visit: /data/data/ch.waut/files/bin on the device itself for partial shell source code and XDA Downloads section and Github for full source code.

Reboot at convenience liberally or sparingly to reseed the entropy pool or as is known in common parlance, for good luck!

Thanks.

Havged source code: 

https://github.com/Openand-I/haveged

Adhoc Payment URL: https://paypal.me/openand/10
