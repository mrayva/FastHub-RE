-keepattributes SourceFile,LineNumberTable
-dontobfuscate
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
# InnerClasses/EnclosingMethod are required for the Signature attribute to resolve
# correctly at runtime; without them, generic type info that Gson/Retrofit read via
# reflection (e.g. TypeToken subclasses, Retrofit service method return types) comes
# back invalid/raw even though Signature itself is kept, causing crashes like
# "Missing type parameter" or "Observable return type must be parameterized".
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Gson TypeToken relies on the generic signature of the (often anonymous) subclass
# it is instantiated as; R8 3.0+ strips/normalizes this unless explicitly told to
# keep it, which crashes Gson's TypeToken.getSuperclassTypeParameter() at runtime
# with "Missing type parameter" even when the enclosing class is otherwise kept.
# See https://github.com/google/gson/blob/main/UPGRADING.md#r8--proguard
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Retrofit does reflection on generic parameters of service interface methods to
# build call adapters (e.g. Observable<User>); without this, R8 can strip the
# return type's generic signature and Retrofit sees a raw, unparameterized type.
# See https://github.com/square/retrofit/blob/master/retrofit/src/main/resources/META-INF/proguard/retrofit2.pro
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,AnnotationDefault
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.AnnotationTarget
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# The rule above only reliably preserves a single level of generic nesting
# (e.g. Observable<User>). Many service methods here return the doubly-nested
# Observable<Response<T>> (used for actions like follow/star/block/delete),
# where RxJava2CallAdapterFactory inspects Response's own type argument at
# runtime and throws "Response must be parameterized as Response<Foo>" if R8
# stripped it back to a raw Response - keeping the wrapper class itself avoids
# that regardless of nesting depth.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

-keepclassmembers class com.fastaccess.** { *; }
-keep class com.fastaccess.** { *; }
-keepclassmembers class com.prettifier.** { *; }
-keep class com.prettifier.** { *; }
-keepclassmembers class com.zzhoujay.** { *; }
-keep class com.zzhoujay.** { *; }

-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE