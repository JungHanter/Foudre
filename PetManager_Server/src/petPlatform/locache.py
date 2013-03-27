import memcache
import time
import md5
from models import UserProfile
"""
Cache Login Class
 
This is login class using memcache and md5
 
package: univ.locache
"""
class Locache():
    """class of login using memcache"""
    hostname = ""
    server   = ""
    def __init__(self, hostname="127.0.0.1", port="11211"):
        self.hostname = "%s:%s" % (hostname, port)
        self.server   = memcache.Client([self.hostname], debug=1)
 
    def set(self, key, value, expiry=900):
        """
        This method is used to set a new value
        in the memcache server.
        """
        self.server.set(key, value, expiry)
 
    def get(self, key):
        """
        This method is used to retrieve a value
        from the memcache server
        """
        return self.server.get(key)
 
    def delete(self, key):
        """
        This method is used to delete a value from the
        memcached server. Lazy delete
        """
        self.server.delete(key)
    
    def authenticate(self, key, value):
        try:
            if self.server.get(key) == value:
                return True
            else:
                try:
                    userProfile = UserProfile.objects.get(user_id=key)
                    if userProfile.login_key == value:
                        self.server.set(key, value, 800)
                        return True
                    else:
                        return False
                except:
                    return False
        except:
            return False            

    def register_key(self, key):
        now = time.localtime()
        s = "%s%s%s%s%s%s"%(now.tm_year, now.tm_mon, now.tm_mday, now.tm_hour, now.tm_min, now.tm_sec)
        value = md5.md5(s).hexdigest()
        try:
            userProfile = UserProfile.objects.get(user_id=key)
            userProfile.login_key = value
            userProfile.save()
            try:
                self.server.set(key, value, 800)
                return value
            except:
                return value
        except:
            return 0
                
        
#        self.server.set(key, value, 800)
#        return self.server.get(key)
#        try:
#            customUser = CustomUser.objects.get(user_id=key)
#            customUser.login_key = value
#            customUser.save()
#            try:
#                self.server.set(key, value, expiry=800)
#                return value
#            except:
#                return 0
#        except:
#            return 0

#if __name__ == '__main__':
#    sample = Example("127.0.0.1");
#    sample.set("name", "david");
#    retrieved = sample.get("name");
#    print retrieved